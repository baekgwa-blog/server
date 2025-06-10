package baekgwa.blogserver.domain.authentication.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.authentication.dto.AuthRequest;
import baekgwa.blogserver.domain.authentication.dto.AuthResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;

/**
 * PackageName : baekgwa.blogserver.domain.authentication.service
 * FileName    : AuthServiceTest
 * Author      : Baekgwa
 * Date        : 2025-06-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-09     Baekgwa               Initial creation
 */
@Transactional
class AuthServiceTest extends SpringBootTestSupporter {

	@DisplayName("로그인 정보를 바탕으로, 유효성 검증 후 Token 을 발급 합니다.")
	@Test
	void login1() {
		// given
		AuthRequest.Login request = AuthRequest.Login.of("loginId", "loginPW");

		// when
		AuthResponse.Login login = authService.login(request);

		// then
		assertThat(login.getAccessToken()).isNotNull();
	}

	@DisplayName("로그인 정보가 틀리면, 오류를 반환합니다.")
	@Test
	void login2() {
		// given
		AuthRequest.Login request = AuthRequest.Login.of("failId", "failPw");

		// when // then
		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_LOGIN_INFO);
	}
}