package baekgwa.blogserver.domain.ai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.ai.controller
 * FileName    : AiController
 * Author      : Baekgwa
 * Date        : 25. 11. 10.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 10.     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
@Tag(name = "Ai Controller", description = "Ai 관련 테스트 컨트롤러")
public class AiController {

	private final EmbeddingService embeddingService;
	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;

	@PostMapping("/search")
	@Operation(summary = "검색어와 유사한 문서 조회 (Retrieval)")
	public List<RetrievalResultDto> searchRetrieval(
		@Valid @RequestBody RetrievalSearchRequest request
	) {
		Integer topK = request.getTopK() == null ? 5 : request.getTopK();
		return embeddingService.searchRetrievalPost(request.getSentence(), topK, request.getFilter());
	}

	@PostMapping("/post/embedding")
	@Operation(summary = "특정 post embedding 후, vector db 에 저장")
	public void embeddingPost(
		@RequestBody EmbeddingPostRequest request
	) {
		for (Long id : request.postIdList()) {
			PostEntity findPost = postRepository.findById(id)
				.orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));

			List<PostTagEntity> findPostTagList = postTagRepository.findAllByPost(findPost);
			List<TagEntity> findTagList = findPostTagList.stream().map(PostTagEntity::getTag).toList();
			embeddingService.createEmbeddingPost(findPost, findTagList);
		}
	}

	public record EmbeddingPostRequest(List<Long> postIdList) {
	}
}
