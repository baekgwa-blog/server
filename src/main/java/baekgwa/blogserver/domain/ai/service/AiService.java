package baekgwa.blogserver.domain.ai.service;

import static baekgwa.blogserver.infra.embedding.service.EmbeddingPostMetadataKeys.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.blogserver.domain.ai.dto.AiRequest;
import baekgwa.blogserver.domain.ai.dto.EmbeddingPostRequest;
import baekgwa.blogserver.infra.embedding.service.EmbeddingService;
import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingJob;
import baekgwa.blogserver.model.embedding.repository.EmbeddingFailureRepository;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.post.tag.entity.PostTagEntity;
import baekgwa.blogserver.model.post.tag.repository.PostTagRepository;
import baekgwa.blogserver.model.tag.entity.TagEntity;
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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

	private final EmbeddingService embeddingService;
	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;
	private final EmbeddingFailureRepository embeddingFailureRepository;
	private final StreamingChatModel streamingChatModel;

	/**
	 * 수동으로, 특정 포스팅 임베딩 후 db 저장
	 * elk 신규 업데이트 후, 기존 document 가 누락되어 있는 경우 사용
	 * @param request
	 */
	@Transactional(readOnly = true)
	public void embeddingPosts(EmbeddingPostRequest request) {
		for (Long id : request.postIdList()) {
			Optional<PostEntity> findOptionalPost = postRepository.findById(id);
			if (findOptionalPost.isEmpty())
				continue;
			PostEntity findPost = findOptionalPost.get();

			List<PostTagEntity> findPostTagList = postTagRepository.findAllByPost(findPost);
			List<TagEntity> findTagList = findPostTagList.stream().map(PostTagEntity::getTag).toList();

			try {
				embeddingService.createEmbeddingPost(findPost, findTagList);
			} catch (Exception e) {
				EmbeddingFailureEntity failure =
					EmbeddingFailureEntity.of(findPost.getId(), e.getMessage(), EmbeddingJob.CREATE);
				embeddingFailureRepository.save(failure);
			}
		}
	}

	public void searchPosts(AiRequest.AiSearchPost request, SseEmitter emitter) {
		// 1. 질문 내용 사전 처리. 용어 통일
		// 미구현

		// 2. Retrieval
		EmbeddingSearchResult<TextSegment> searchResult =
			embeddingService.searchRetrievalPost(request.getSentence(), request.getFilter());

		// 3. Question to LLM
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
}
