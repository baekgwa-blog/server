package baekgwa.blogserver.global.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * PackageName : baekgwa.blogserver.global.cache
 * FileName    : CacheType
 * Author      : Baekgwa
 * Date        : 26. 1. 24.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 24.     Baekgwa               Initial creation
 */
@Getter
@AllArgsConstructor
public enum CacheType {
	POST_LIST(CacheNames.POST_LIST, 60 * 24); // 1일, 1440분

	private final String cacheName;
	private final int ttlMinutes;

	@UtilityClass
	public static class CacheNames {
		public static final String POST_LIST = "posts:list";
	}
}