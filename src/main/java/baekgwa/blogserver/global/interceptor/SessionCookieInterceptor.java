package baekgwa.blogserver.global.interceptor;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import baekgwa.blogserver.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.interceptor
 * FileName    : SessionCookieInterceptor
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : 비로그인 방문자 세션 쿠키(sid) 발급 인터셉터
 *               sid 쿠키가 없는 요청에 UUID 기반 세션 ID 를 자동 발급합니다.
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class SessionCookieInterceptor implements HandlerInterceptor {

	private final Environment environment;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (CookieUtil.extractSessionId(request).isEmpty()) {
			String newSid = UUID.randomUUID().toString();
			response.addCookie(CookieUtil.createSessionCookie(newSid, isProdProfile()));
			request.setAttribute(CookieUtil.SESSION_COOKIE_NAME, newSid);
		}
		return true;
	}

	private boolean isProdProfile() {
		return Arrays.asList(environment.getActiveProfiles()).contains("prod");
	}
}
