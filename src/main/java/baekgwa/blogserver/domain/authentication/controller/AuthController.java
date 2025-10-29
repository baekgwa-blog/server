package baekgwa.blogserver.domain.authentication.controller;

import java.util.Arrays;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.blogserver.domain.authentication.dto.AuthRequest;
import baekgwa.blogserver.domain.authentication.dto.AuthResponse;
import baekgwa.blogserver.domain.authentication.service.AuthService;
import baekgwa.blogserver.global.environment.AuthProperties;
import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.authentication.controller
 * FileName    : AuthController
 * Author      : Baekgwa
 * Date        : 2025-06-04
 * Description : 로그인 등 처리
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-04     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "회원 인증")
public class AuthController {

	private final AuthService authService;
	private final AuthProperties authProperties;
	private final Environment environment;

	@PostMapping("/login")
	@Operation(summary = "로그인")
	public BaseResponse<Void> login(
		@Valid @RequestBody AuthRequest.Login requestDto,
		HttpServletResponse response
	) {
		// 로그인 처리
		AuthResponse.Login authResponseLogin = authService.login(requestDto);

		Cookie accessTokenCookie = CookieUtil.createAccessTokenCookie(
			authResponseLogin.getAccessToken(),
			authProperties.getTokenExpiration().intValue(),
			isProdProfile()
		);

		response.addCookie(accessTokenCookie);

		return BaseResponse.success(SuccessCode.LOGIN_SUCCESS);
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public BaseResponse<Void> logout(HttpServletResponse response) {
		Cookie deleteCookie = CookieUtil.createDeleteCookie(isProdProfile());

		response.addCookie(deleteCookie);

		return BaseResponse.success(SuccessCode.LOGOUT_SUCCESS);
	}

	private boolean isProdProfile() {
		return Arrays.asList(environment.getActiveProfiles()).contains("prod");
	}
}
