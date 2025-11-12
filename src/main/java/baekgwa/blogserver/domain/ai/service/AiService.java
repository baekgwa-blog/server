package baekgwa.blogserver.domain.ai.service;

import static baekgwa.blogserver.infra.embedding.service.EmbeddingPostMetadataKeys.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.blogserver.domain.ai.dto.AiRequest;
import baekgwa.blogserver.domain.ai.dto.EmbeddingPostRequest;
import baekgwa.blogserver.domain.ai.dto.RetrievalResultDto;
import baekgwa.blogserver.domain.ai.dto.RetrievalSearchRequest;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.infra.embedding.service.EmbeddingService;
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
	private final StreamingChatModel streamingChatModel;

	public List<RetrievalResultDto> searchRetrievalPost(RetrievalSearchRequest request) {
		EmbeddingSearchResult<TextSegment> searchResult =
			embeddingService.searchRetrievalPost(request.getSentence(), request.getFilter());

		return searchResult.matches().stream()
			.map(match -> RetrievalResultDto.from(
				match.score(),
				match.embedded().text(),
				match.embedded().metadata().toMap()
			))
			.toList();
	}

	@Transactional(readOnly = true)
	public void embeddingPosts(EmbeddingPostRequest request) {
		for (Long id : request.postIdList()) {
			PostEntity findPost = postRepository.findById(id)
				.orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));

			List<PostTagEntity> findPostTagList = postTagRepository.findAllByPost(findPost);
			List<TagEntity> findTagList = findPostTagList.stream().map(PostTagEntity::getTag).toList();
			embeddingService.createEmbeddingPost(findPost, findTagList);
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
		// 1. SystemMessage 작성
		String systemPrompt = """
			당신은 '백과'의 블로그 콘텐츠를 기반으로 사용자의 질문에 답변하는 AI 어시스턴트입니다.
			
			다음 규칙을 반드시 따르세요:
			
			1. 당신의 모든 답변은 반드시 제공된 문서(검색 결과)에 기반해야 합니다.
			   - 검색된 문서 내용에 근거가 없으면, 새로운 정보를 생성하거나 추측하지 마세요.
			   - 관련 문서가 전혀 없으면, 다음 문장을 그대로 출력하세요:
			     "현재 백과 블로그에는 관련 게시글이 없습니다 🥲"
			
			2. 사용자의 질문에 대해 문서에서 얻은 내용을 바탕으로 간결하고 명확하게 설명하세요.
			
			3. 답변이 끝난 후, 사용자가 참고할 수 있도록 관련 블로그 포스트 목록을 함께 제공합니다.
			   - 문서의 메타데이터에서 `SOURCE`와 `TITLE` 값을 사용하여 아래 형식으로 표시하세요:
			     - [TITLE](SOURCE)
			   - 여러 문서가 있을 경우, 관련도가 높은 순으로 3개까지만 나열하세요.
			
			4. 문서 내용 요약이나 설명은 자연스럽고, 링크 섹션은 명확히 구분되도록 출력하세요.
			
			출력 형식 예시:
			---
			멀티스레드는 하나의 프로세스 안에서 여러 실행 흐름을 동시에 수행하는 기술입니다.
			이를 통해 CPU 자원을 효율적으로 활용할 수 있으며, 병렬 처리가 필요한 상황에서 성능 향상을 기대할 수 있습니다.
			
			**관련 게시글**
			- [자바 멀티스레드 기본 개념](https://baekqa.dev/posts/multithread-basic)
			- [Thread 클래스와 Runnable 인터페이스 차이](https://baekqa.dev/posts/thread-vs-runnable)
			---
			""";

		// 2. 검색된 문서 내용을 프롬프트에 포함
		String context = buildRetrievalContext(retrievalResult);
		String fullPrompt = systemPrompt + "\n\n[검색된 문서 내용]\n" + context;

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
					emitter.send(SseEmitter.event()
						.name("message")
						.data(partialResponse));
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
