package baekgwa.blogserver.global.util;

import static baekgwa.blogserver.global.constants.TokenConstant.*;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;

/**
 * PackageName : baekgwa.blogserver.global.util
 * FileName    : CookieUtil
 * Author      : Baekgwa
 * Date        : 25. 10. 29.
 * Description : 쿠키 생성을 담당하는 유틸리티 클래스
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 10. 29.     Baekgwa               Initial creation
 */
@UtilityClass
public class CookieUtil {

	/**
	 * 액세스 토큰 쿠키를 생성합니다. (로그인 시 사용)
	 *
	 * @param token    액세스 토큰 값
	 * @param maxAge   쿠키 유효 시간 (초)
	 * @param isProd   'prod' 프로필 활성화 여부
	 * @return 생성된 Cookie 객체
	 */
	public static Cookie createAccessTokenCookie(String token, int maxAge, boolean isProd) {
		return createBaseCookie(token, maxAge, isProd);
	}

	/**
	 * 쿠키를 삭제하는 쿠키를 생성합니다. (로그아웃 시 사용)
	 *
	 * @param isProd   'prod' 프로필 활성화 여부
	 * @return 생성된 Cookie 객체 (maxAge=0)
	 */
	public static Cookie createDeleteCookie(boolean isProd) {
		return createBaseCookie(null, 0, isProd);
	}

	/**
	 * 쿠키 생성의 공통 로직을 처리합니다.
	 *
	 * @param value    쿠키 값 (null일 경우 삭제 쿠키)
	 * @param maxAge   쿠키 유효 시간 (0일 경우 즉시 만료)
	 * @param isProd   'prod' 프로필 활성화 여부
	 * @return 생성된 Cookie 객체
	 */
	private static Cookie createBaseCookie(String value, int maxAge, boolean isProd) {
		Cookie cookie = new Cookie(ACCESS_TOKEN_KEY, value);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);

		if (isProd) {
			cookie.setDomain("baekgwa.site");
			cookie.setSecure(true);
		}

		return cookie;
	}
}