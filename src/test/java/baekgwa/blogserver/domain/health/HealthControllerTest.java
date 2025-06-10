package baekgwa.blogserver.domain.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;

/**
 * PackageName : baekgwa.blogserver.domain.health
 * FileName    : HealthControllerTest
 * Author      : Baekgwa
 * Date        : 2025-06-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-09     Baekgwa               Initial creation
 */
@Transactional
class HealthControllerTest extends SpringBootTestSupporter {

	@DisplayName("헬스 체크 정상 등록 확인")
	@Test
	void healthCheck() throws Exception {
		// given

		// when
		ResultActions perform = mockMvc.perform(get("/health"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@WithMockUser
	@DisplayName("로그인 상태 확인")
	@Test
	void loginHealthCheck() throws Exception {
		// given

		// when
		ResultActions perform = mockMvc.perform(get("/health/login"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("로그인 상태가 아니라면, 로그인 유무 확인 시, 실패합니다.")
	@Test
	void loginHealthCheckFail() throws Exception {
		// given

		// when
		ResultActions perform = mockMvc.perform(get("/health/login"));

		// then
		perform.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.NEED_LOGIN.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.NEED_LOGIN.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}
}