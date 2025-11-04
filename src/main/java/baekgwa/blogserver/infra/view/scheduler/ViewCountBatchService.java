package baekgwa.blogserver.infra.view.scheduler;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.infra.view.store.ViewCountStore;
import baekgwa.blogserver.infra.view.type.ViewDomain;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.view.scheduler
 * FileName    : ViewCountBatchService
 * Author      : Baekgwa
 * Date        : 25. 11. 4.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 4.     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountBatchService {

	private final ViewCountStore viewCountStore;
	private final PostRepository postRepository;

	@Transactional
	public void synchronizeViewCounts() {
		log.debug("게시글 조회수 동기화 시작");

		Map<Long, Long> postViewMap = viewCountStore.getAllViewCount(ViewDomain.POST);

		if (postViewMap.isEmpty()) {
			log.debug("적립된 조회수가 없습니다. 종료합니다.");
			return;
		}

		postRepository.bulkUpdateViewCounts(postViewMap);

		viewCountStore.clearViewCount(ViewDomain.POST);
		log.debug("게시글 조회수 동기화 완료. count : {}", postViewMap.size());
	}
}
