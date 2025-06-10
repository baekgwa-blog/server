package baekgwa.blogserver.domain.authentication.controller;

import static baekgwa.blogserver.global.constants.TokenConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.authentication.dto.AuthRequest;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import jakarta.servlet.http.Cookie;

/**
 * PackageName : baekgwa.blogserver.domain.authentication.controller
 * FileName    : AuthControllerTest
 * Author      : Baekgwa
 * Date        : 2025-06-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-09     Baekgwa               Initial creation
 */
@Transactional
class AuthControllerTest extends SpringBootTestSupporter {

	@DisplayName("로그인 시, 쿠키로 회원 token 이 발급 됩니다.")
	@Test
	void login1() throws Exception {
		// given
		AuthRequest.Login request = AuthRequest.Login.of("loginId", "loginPW");

		// when
		ResultActions perform = mockMvc.perform(post("/auth/login")
			.content(objectMapper.writeValueAsString(request))
			.contentType(MediaType.APPLICATION_JSON));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.LOGIN_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.LOGIN_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());

		// 쿠키 검증
		Cookie cookie = perform.andReturn().getResponse().getCookie(ACCESS_TOKEN_KEY);
		assertThat(cookie).isNotNull();
	}

	@DisplayName("잘못된 로그인 정보로 로그인 시, 로그인이 실패합니다.")
	@Test
	void login2() throws Exception {
		// given
		AuthRequest.Login request = AuthRequest.Login.of("failId", "failPw");

		// when
		ResultActions perform = mockMvc.perform(post("/auth/login")
			.content(objectMapper.writeValueAsString(request))
			.contentType(MediaType.APPLICATION_JSON));

		// then
		perform.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_LOGIN_INFO.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_LOGIN_INFO.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());

		// 쿠키 검증
		Cookie cookie = perform.andReturn().getResponse().getCookie(ACCESS_TOKEN_KEY);
		assertThat(cookie).isNull();
	}

	@WithMockUser
	@DisplayName("로그아웃을 진행합니다.")
	@Test
	void logout1() throws Exception {
		// given

		// when
		ResultActions perform = mockMvc.perform(post("/auth/logout"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.LOGOUT_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.LOGOUT_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());

		Cookie deletedCookie = perform.andReturn().getResponse().getCookie(ACCESS_TOKEN_KEY);
		assertThat(deletedCookie).isNotNull();
		assertThat(deletedCookie.getValue()).isNull();
		assertThat(deletedCookie.getMaxAge()).isZero();
	}
}