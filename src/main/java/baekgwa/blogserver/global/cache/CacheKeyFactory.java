package baekgwa.blogserver.global.cache;

import java.util.Objects;

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

	private static final String ALL = "all";

	/**
	 * 메인페이지 에서, 글 목록 캐싱을 위한 캐시 키
	 */
	public String getPostListKey(String category, int page, int size, PostListSort sort) {
		return String.format("category:%s:page:%d:size:%d:sort:%s",
			Objects.requireNonNullElse(category, ALL),
			page,
			size,
			Objects.requireNonNullElse(sort, PostListSort.LATEST)
		);
	}

	/**
	 * 게시글 상세 조회 시, 캐싱을 위한 키
	 */
	public String getPostDetailKey(String slug) {
		return String.format("slug:%s", slug);
	}

	/**
	 * 전체 카테고리 조회용 캐시 키
	 */
	public String getCategoryListKey() {
		return ALL;
	}

	/**
	 * Stack Post 연관글 조회용 캐시 키
	 */
	public String getRelativeStackPostListKey(Long stackId) {
		return String.format("stack:%s", stackId);
	}
}
