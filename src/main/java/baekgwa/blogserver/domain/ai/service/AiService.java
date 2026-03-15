package baekgwa.blogserver.domain.ai.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.blogserver.domain.ai.dto.AiRequest;
import baekgwa.blogserver.domain.ai.dto.AiResponse;
import baekgwa.blogserver.infra.embedding.service.EmbeddingService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.domain.ai.service
 * FileName    : AiService
 * Author      : Baekgwa
 * Date        : 25. 11. 12.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 12.     Baekgwa               Initial creation
 * 2026-03-15     Baekgwa               수동 임베딩 기능 제거 (Data Pipeline 이관), EmbeddingPostMetadataKeys 상수 인라인화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

	private static final String TITLE = "title";
	private static final String SOURCE = "source";

	private final EmbeddingService embeddingService;
	private final StreamingChatModel streamingChatModel;

	private final Executor taskExecutor;
	private final RestClient elasticSearchRestClient;
	@Qualifier("openAiRestTemplate")
	private final RestTemplate openAiRestTemplate;

	public void searchPosts(AiRequest.AiSearchPost request, SseEmitter emitter) {
		EmbeddingSearchResult<TextSegment> searchResult =
				embeddingService.searchRetrievalPost(request.getSentence(), request.getFilter());

		streamToEmitter(request, emitter, searchResult);
	}

	private void streamToEmitter(
			AiRequest.AiSearchPost request,
			SseEmitter emitter,
			EmbeddingSearchResult<TextSegment> retrievalResult
	) {
		// 1. SystemMessage 작성 (기본 역할 부여)
		String systemPrompt = """
				당신은 '백과'의 블로그 콘텐츠를 기반으로 사용자의 질문에 답변하는 AI 어시턴트입니다.
				당신의 모든 답변은 반드시 제공된 문서(검색 결과)에 기반해야 합니다.
				""";

		// 2. 검색된 문서 내용을 프롬프트에 포함
		String context = buildRetrievalContext(retrievalResult);

		String finalInstructions = """
				[검색된 문서 내용]
				%s
				
				[최종 지시 사항]
				아래 규칙에 따라 Markdown 형식으로 가독성 높게 답변하세요.
				
				---
				
				## 🔒 절대 규칙
				
				1. 반드시 검색된 문서 내용에 기반하여 답변하세요.
				   문서 내용에 없는 정보는 절대 생성하거나 추측하지 마세요.
				   문서가 없다면 다음 문장을 그대로 출력하세요:
				   **"현재 백과 블로그에는 관련 포스팅이 없습니다 😔"**
				
				
				2. **모든 단락(문단)은 엔터 2번(= 빈 줄 1개)을 사용하여 구분해야 합니다.**
				   즉, 단락과 단락 사이에는 반드시 **빈 줄이 한 줄 들어가야 합니다.**
				   예)
				   문단 A 내용
				
				   문단 B 내용
				
				   문단 C 내용
				
				
				3. **Markdown을 적극적으로 사용하여 가독성을 높이세요.**
				   - 제목: `##`, `###`
				   - 리스트: `-`
				   - 강조: `**굵게**`
				   - 필요 시 코드블록도 사용 가능
				
				
				4. 답변은 아래 구조를 반드시 따르세요:
				
				---
				### 📌 요약
				(문서 기반 핵심 요약 2~3줄)
				
				
				### 🔗 관련 포스트
				아래 형식으로 최대 3개
				- [`TITLE`](SOURCE)
				- [`TITLE`](SOURCE)
				- [`TITLE`](SOURCE)
				---
				
				
				5. 링크는 반드시 다음 형태를 지켜야 합니다.
				`[TITLE](SOURCE)`
				절대 깨뜨리지 마세요.
				
				---
				
				출력 예시:
				---
				### 📌 요약
				멀티스레드는 하나의 프로세스 내부에서 여러 실행 흐름을 동시에 수행하는 기술입니다.
				
				
				### 🔗 관련 포스트
				- [자바 멀티스레드 기본 개념](https://baekqa.dev/posts/multithread-basic)
				- [Thread 클래스와 Runnable 인터페이스 차이](https://baekqa.dev/posts/thread-vs-runnable)
				- [멀티스레드 동기화 기초](https://baekqa.dev/posts/multithread-sync)
				---
				""";

		String formattedInstructions = String.format(finalInstructions, context);
		String fullPrompt = systemPrompt + formattedInstructions;

		// 3. 메시지 구성
		List<ChatMessage> messageList = List.of(
				SystemMessage.from(fullPrompt),
				UserMessage.from(request.getSentence())
		);

		// 4. LLM 스트리밍 핸들러 생성
		StreamingChatResponseHandler handler = new StreamingChatResponseHandler() {
			@Override
			public void onPartialResponse(String partialResponse) {
				try {
					Map<String, String> dataMap = Map.of("token", partialResponse);

					emitter.send(SseEmitter.event()
							.name("message")
							.data(dataMap));

				} catch (IOException disconnect) {
					emitter.completeWithError(disconnect);
				}
			}

			@Override
			public void onCompleteResponse(ChatResponse completeResponse) {
				try {
					emitter.send(SseEmitter.event().name("done").data("[DONE]"));
					emitter.complete();
				} catch (IOException e) {
					emitter.completeWithError(e);
				}
			}

			@Override
			public void onError(Throwable error) {
				emitter.completeWithError(error);
			}
		};

		// 5. 스트리밍 요청 실행
		streamingChatModel.chat(messageList, handler);
	}

	private String buildRetrievalContext(EmbeddingSearchResult<TextSegment> result) {
		StringBuilder sb = new StringBuilder();
		for (EmbeddingMatch<TextSegment> match : result.matches()) {
			TextSegment segment = match.embedded();
			Map<String, Object> meta = segment.metadata().toMap();

			String title = meta.getOrDefault(TITLE, "제목 없음").toString();
			String source = meta.getOrDefault(SOURCE, "URL 없음").toString();

			sb.append("### ").append(title).append("\n")
					.append("TITLE : ").append(title).append("\n")
					.append("SOURCE : ").append(source).append("\n")
					.append(segment.text()).append("\n\n");
		}
		return sb.toString();
	}

	public AiResponse.AiHealthCheck healthCheck() {
		// 1. elk connection check
		CompletableFuture<AiResponse.HealthStatus> dbFuture = CompletableFuture.supplyAsync(
				this::checkElkConnection,
				taskExecutor
		);

		// 2. llm connection check
		CompletableFuture<AiResponse.HealthStatus> llmFuture = CompletableFuture.supplyAsync(
				this::checkOpenAiConnection,
				taskExecutor
		);

		AiResponse.HealthStatus dbStatus = dbFuture.join();
		AiResponse.HealthStatus llmStatus = llmFuture.join();
		boolean isAvailable =
				(dbStatus == AiResponse.HealthStatus.UP) && (llmStatus == AiResponse.HealthStatus.UP);

		return AiResponse.AiHealthCheck.builder()
				.database(dbStatus)
				.llm(llmStatus)
				.isAvailable(isAvailable)
				.build();
	}

	private AiResponse.HealthStatus checkElkConnection() {
		try {
			Response response = elasticSearchRestClient.performRequest(new Request("HEAD", "/"));

			log.debug("[HealthCheck] VectorDB Connection Success");

			return response.getStatusLine().getStatusCode() == 200 ?
					AiResponse.HealthStatus.UP :
					AiResponse.HealthStatus.DOWN;
		} catch (Exception e) {
			log.debug("[HealthCheck] ELK Connection Failed: {}", e.getMessage());
			return AiResponse.HealthStatus.DOWN;
		}
	}

	private AiResponse.HealthStatus checkOpenAiConnection() {
		try {
			// models 를 호출해서 연결이 가능한지 확인. 이게 제일 저렴
			ResponseEntity<String> response = openAiRestTemplate.getForEntity("/models", String.class);

			log.debug("[HealthCheck] OpenAI Connection Success");

			return response.getStatusCode().is2xxSuccessful() ?
					AiResponse.HealthStatus.UP :
					AiResponse.HealthStatus.DOWN;
		} catch (Exception e) {
			log.debug("[HealthCheck] OpenAI Connection Failed: {}", e.getMessage());
			return AiResponse.HealthStatus.DOWN;
		}
	}
}
