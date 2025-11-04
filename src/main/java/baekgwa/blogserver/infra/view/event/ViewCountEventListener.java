package baekgwa.blogserver.infra.view.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import baekgwa.blogserver.infra.view.type.ViewDomain;
import baekgwa.blogserver.infra.view.updater.ViewCountUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.global.listener
 * FileName    : ViewCountEventListener
 * Author      : Baekgwa
 * Date        : 25. 11. 4.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 4.     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountEventListener {

	private final ViewCountUpdater viewCountUpdater;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePostViewEvent(PostViewEvent event) {
		viewCountUpdater.updateViewCount(ViewDomain.POST, event.postId(), event.remoteAddr());
	}
}
