package baekgwa.blogserver.global.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.filter
 * FileName    : AuthenticationFilter
 * Author      : Baekgwa
 * Date        : 2025-06-04
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-04     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 1. Token cookie 에서 추출
		String accessToken = Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals("accessToken"))
			.map(Cookie::getValue)
			.findFirst()
			.orElse(null);

		// 2. token 유무 검증
		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 3. token 유효성 검증
		if (jwtUtil.isExpired(accessToken)) {
			// expired 되었다면, 로그인 만료 응답 진행
			ErrorCode errorCode = ErrorCode.EXPIRED_INVALID_TOKEN;
			BaseResponse<Void> errorResponse = BaseResponse.fail(errorCode);
			response.setContentType("application/json");
			response.setStatus(errorCode.getStatus().value());
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
			return;
		}

		// 4. Security Context 에 인증 정보 추가
		Authentication authentication =
			new UsernamePasswordAuthenticationToken("admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 5. 완료 후, 다음 filter 로 이동
		filterChain.doFilter(request, response);
	}
}
