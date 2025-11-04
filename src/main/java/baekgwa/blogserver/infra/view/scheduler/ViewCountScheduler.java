package baekgwa.blogserver.infra.view.scheduler;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.infra.view.scheduler
 * FileName    : ViewCountScheduler
 * Author      : Baekgwa
 * Date        : 25. 11. 4.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 4.     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

	private final ViewCountBatchService viewCountBatchService;

	@Scheduled(fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
	public void scheduleViewCountSync() {
		viewCountBatchService.synchronizeViewCounts();
	}
}
