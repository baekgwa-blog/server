package baekgwa.blogserver.domain.stack.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.stack.dto.StackResponse;
import baekgwa.blogserver.global.cache.CacheType;
import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;
import baekgwa.blogserver.model.stack.post.repository.StackPostRepository;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.domain.stack.service
 * FileName    : StackCacheService
 * Author      : Baekgwa
 * Date        : 26. 1. 25.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 25.     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StackCacheService {

	private final StackPostRepository stackPostRepository;

	@Cacheable(
		cacheNames = CacheType.CacheNames.STACK_RELATIVE_POST_LIST,
		key = "@cacheKeyFactory.getRelativeStackPostListKey(#findStack.getId)",
		unless = "#result == null"
	)
	@Transactional(readOnly = true)
	public StackResponse.StackInfo getRelativeStackPostList(StackEntity findStack) {
		log.debug("[Cache Miss] Get Relative Stack Post, stack Title:{}, stack id:{}", findStack.getTitle(), findStack.getId());

		List<StackPostEntity> findStackPostList = stackPostRepository.findAllByStack(findStack);

		List<StackResponse.StackPostInfo> stackPostInfoList = findStackPostList.stream()
			.map(StackResponse.StackPostInfo::of)
			.sorted(Comparator.comparing(StackResponse.StackPostInfo::getSequence))
			.toList();

		return StackResponse.StackInfo.of(findStack, stackPostInfoList);
	}

	@Transactional
	@CacheEvict(
		cacheNames = CacheType.CacheNames.STACK_RELATIVE_POST_LIST,
		key = "@cacheKeyFactory.getRelativeStackPostListKey(#stackId)"
	)
	public void deleteStackPostLink(Long stackId, Long postId) {
		log.info("Evict Stack Cache & Delete Link -> stackId: {}, postId: {}", stackId, postId);
		stackPostRepository.deleteByStackIdAndPostId(stackId, postId);
	}

	@CacheEvict(
		cacheNames = CacheType.CacheNames.STACK_RELATIVE_POST_LIST,
		key = "@cacheKeyFactory.getRelativeStackPostListKey(#stackId)"
	)
	public void evictRelativeStackPostList(Long stackId) {
		log.info("Evict Stack Cache stackId: {}", stackId);
	}
}
