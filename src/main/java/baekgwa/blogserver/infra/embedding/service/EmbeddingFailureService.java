package baekgwa.blogserver.infra.embedding.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingJob;
import baekgwa.blogserver.model.embedding.repository.EmbeddingFailureRepository;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.post.tag.entity.PostTagEntity;
import baekgwa.blogserver.model.post.tag.repository.PostTagRepository;
import baekgwa.blogserver.model.tag.entity.TagEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.service
 * FileName    : EmbeddingFailureService
 * Author      : Baekgwa
 * Date        : 25. 11. 15.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 15.     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingFailureService {

	private final EmbeddingService embeddingService;
	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;
	private final EmbeddingFailureRepository embeddingFailureRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processSingleFailure(EmbeddingFailureEntity failure) {
		Long postId = failure.getPostId();

		if (failure.getEmbeddingJob().equals(EmbeddingJob.CREATE)) {
			retryCreateEmbedding(failure, postId);
		} else if (failure.getEmbeddingJob().equals(EmbeddingJob.DELETE)) {
			retryDeleteEmbedding(failure, postId);
		}

		// 정상적인 경우, 해당 failure 는 처리 완료로 보고, 삭제 처리
		embeddingFailureRepository.deleteById(failure.getId());
	}

	private void retryCreateEmbedding(EmbeddingFailureEntity failure, Long postId) {
		PostEntity findPost = postRepository.findById(postId).orElse(null);
		if (findPost == null) {
			log.warn("Post not found for retry, deleting failure record: postId={}", postId);
			return;
		}

		List<TagEntity> findTagList = postTagRepository.findAllByPost(findPost)
			.stream()
			.map(PostTagEntity::getTag)
			.toList();

		try {
			embeddingService.createEmbeddingPost(findPost, findTagList);
		} catch (Exception e) {
			failure.updateReason(e.getMessage());
			throw e;
		}
	}

	private void retryDeleteEmbedding(EmbeddingFailureEntity failure, Long postId) {
		try {
			embeddingService.deleteEmbeddingPost(postId);
		} catch (Exception e) {
			failure.updateReason(e.getMessage());
			throw e;
		}
	}
}
