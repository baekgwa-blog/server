package baekgwa.blogserver.global.util;

import static baekgwa.blogserver.global.constants.TokenConstant.*;

import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

/**
 * PackageName : baekgwa.blogserver.global.util
 * FileName    : CookieUtil
 * Author      : Baekgwa
 * Date        : 25. 10. 29.
 * Description : 쿠키 생성 및 추출을 담당하는 유틸리티 클래스
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 10. 29.     Baekgwa               Initial creation
 * 2026-03-15     Baekgwa               방문자 세션 쿠키(sid) 관련 메서드 통합 (SessionCookieUtil 흡수)
 */
@UtilityClass
public class CookieUtil {

	public static final String SESSION_COOKIE_NAME = "sid";
	private static final int SESSION_MAX_AGE_SECONDS = 30 * 24 * 60 * 60; // 30일

	/**
	 * 액세스 토큰 쿠키를 생성합니다. (로그인 시 사용)
	 *
	 * @param token    액세스 토큰 값
	 * @param maxAge   쿠키 유효 시간 (초)
	 * @param isProd   'prod' 프로필 활성화 여부
	 * @return 생성된 Cookie 객체
	 */
	public static Cookie createAccessTokenCookie(String token, int maxAge, boolean isProd) {
		return createBaseCookie(ACCESS_TOKEN_KEY, token, maxAge, isProd);
	}

	/**
	 * 쿠키를 삭제하는 쿠키를 생성합니다. (로그아웃 시 사용)
	 *
	 * @param isProd   'prod' 프로필 활성화 여부
	 * @return 생성된 Cookie 객체 (maxAge=0)
	 */
	public static Cookie createDeleteCookie(boolean isProd) {
		return createBaseCookie(ACCESS_TOKEN_KEY, null, 0, isProd);
	}

	/**
	 * 방문자 세션 쿠키를 생성합니다. (비로그인 방문자 행동 추적용)
	 *
	 * @param sid      세션 UUID
	 * @param isProd   'prod' 프로필 활성화 여부
	 * @return 생성된 Cookie 객체
	 */
	public static Cookie createSessionCookie(String sid, boolean isProd) {
		return createBaseCookie(SESSION_COOKIE_NAME, sid, SESSION_MAX_AGE_SECONDS, isProd);
	}

	/**
	 * 요청에서 방문자 세션 ID를 추출합니다.
	 *
	 * @param req HttpServletRequest
	 * @return 세션 ID Optional (없으면 empty)
	 */
	public static Optional<String> extractSessionId(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			Optional<String> fromCookie = Arrays.stream(cookies)
				.filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
				.map(Cookie::getValue)
				.findFirst();
			if (fromCookie.isPresent()) {
				return fromCookie;
			}
		}
		Object attr = req.getAttribute(SESSION_COOKIE_NAME);
		return attr != null ? Optional.of(attr.toString()) : Optional.empty();
	}

	private static Cookie createBaseCookie(String name, String value, int maxAge, boolean isProd) {
		Cookie cookie = new Cookie(name, value);
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
