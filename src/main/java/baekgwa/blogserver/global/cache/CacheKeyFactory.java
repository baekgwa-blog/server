package baekgwa.blogserver.global.cache;

import org.springframework.stereotype.Component;

import baekgwa.blogserver.domain.post.type.PostListSort;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.cache
 * FileName    : CacheKeyFactory
 * Author      : Baekgwa
 * Date        : 26. 1. 24.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 24.     Baekgwa               Initial creation
 */
@Component("cacheKeyFactory")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheKeyFactory {

	private static final String NONE = "none";
	private static final String ALL = "all";

	/**
	 * 메인페이지 에서, 글 목록 캐싱을 위한 캐시 키
	 */
	public String getPostListKey(String keyword, String category, int page, int size, PostListSort sort) {
		return String.format("keyword:%s:category:%s:page:%d:size:%d:sort:%s",
			keyword != null ? keyword : NONE,
			category != null ? category : ALL,
			page,
			size,
			sort
		);
	}
}
