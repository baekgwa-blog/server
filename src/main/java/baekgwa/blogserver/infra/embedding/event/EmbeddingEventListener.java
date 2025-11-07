package baekgwa.blogserver.infra.embedding.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.blogserver.infra.embedding.service.EmbeddingService;
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

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleEmbeddingNewPostEvent(EmbeddingPostEvent event) {
		log.debug("신규 포스트 id={} title={}, embedding event 실시", event.post().getId(), event.post().getTitle());
		embeddingService.embeddingPostToVector(event.post(), event.tagList());
	}
}
