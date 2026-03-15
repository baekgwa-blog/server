package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import baekgwa.blogserver.global.interceptor.SessionCookieInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : SessionWebMvcConfig
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : 세션 쿠키 인터셉터 등록 설정 (모든 프로파일에서 활성화)
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
@Configuration
@RequiredArgsConstructor
public class SessionWebMvcConfig implements WebMvcConfigurer {

	private final SessionCookieInterceptor sessionCookieInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionCookieInterceptor)
			.addPathPatterns("/post/**");
	}
}
