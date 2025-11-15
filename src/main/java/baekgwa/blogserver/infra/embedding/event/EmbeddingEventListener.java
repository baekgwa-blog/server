package baekgwa.blogserver.infra.embedding.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.blogserver.infra.embedding.service.EmbeddingService;
import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingJob;
import baekgwa.blogserver.model.embedding.repository.EmbeddingFailureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.event
 * FileName    : EmbeddingEventListener
 * Author      : Baekgwa
 * Date        : 25. 11. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingEventListener {

	private final EmbeddingService embeddingService;
	private final EmbeddingFailureRepository embeddingFailureRepository;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleEmbeddingNewPostEvent(EmbeddingCreatePostEvent event) {
		try {
			embeddingService.createEmbeddingPost(event.post(), event.tagList());
		} catch (Exception e) {
			EmbeddingFailureEntity failure =
				EmbeddingFailureEntity.of(event.post().getId(), e.getMessage(), EmbeddingJob.CREATE);
			embeddingFailureRepository.save(failure);
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleEmbeddingDeletePost(EmbeddingDeletePostEvent event) {
		try {
			embeddingService.deleteEmbeddingPost(event.postId());
		} catch (Exception e) {
			EmbeddingFailureEntity failure =
				EmbeddingFailureEntity.of(event.postId(), e.getMessage(), EmbeddingJob.DELETE);
			embeddingFailureRepository.save(failure);
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleEmbeddingUpdatePost(EmbeddingUpdatePostEvent event) {
		// 1. 삭제
		try {
			embeddingService.deleteEmbeddingPost(event.post().getId());
		} catch (Exception e) {
			EmbeddingFailureEntity failure =
				EmbeddingFailureEntity.of(event.post().getId(), e.getMessage(), EmbeddingJob.DELETE);
			embeddingFailureRepository.save(failure);
			return;
		}

		// 2. 생성
		try {
			embeddingService.createEmbeddingPost(event.post(), event.tagList());
		} catch (Exception e) {
			EmbeddingFailureEntity failure =
				EmbeddingFailureEntity.of(event.post().getId(), e.getMessage(), EmbeddingJob.CREATE);
			embeddingFailureRepository.save(failure);
		}
	}
}
