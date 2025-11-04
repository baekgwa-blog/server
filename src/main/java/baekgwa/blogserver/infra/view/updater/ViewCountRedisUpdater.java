package baekgwa.blogserver.infra.view.updater;

import org.springframework.stereotype.Component;

import baekgwa.blogserver.infra.view.store.ViewCountStore;
import baekgwa.blogserver.infra.view.type.ViewDomain;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.infra.view.updater
 * FileName    : ViewCountRedisUpdater
 * Author      : Baekgwa
 * Date        : 25. 11. 3.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 3.     Baekgwa             Redis 를 활용한 ViewCountUpdater 구현
 */
@Component
@RequiredArgsConstructor
public class ViewCountRedisUpdater implements ViewCountUpdater {

	private final ViewCountStore viewCountStore;

	@Override
	public void updateViewCount(ViewDomain viewDomain, Long id, String remoteAddr) {
		viewCountStore.incrementViewCount(viewDomain, id, remoteAddr);
	}
}
