package baekgwa.blogserver.domain.tag.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.tag.dto.TagRequest;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;

/**
 * PackageName : baekgwa.blogserver.domain.tag.controller
 * FileName    : TagControllerTest
 * Author      : Baekgwa
 * Date        : 2025-06-17
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-17     Baekgwa               Initial creation
 */
@Transactional
class TagControllerTest extends SpringBootTestSupporter {

	@WithMockUser
	@DisplayName("새로운 태그를 생성합니다.")
	@Test
	void createTag1() throws Exception {
		// given
		TagRequest.CreateTag request = TagRequest.CreateTag.of("태그1");

		// when
		ResultActions perform = mockMvc.perform(post("/tag")
			.content(objectMapper.writeValueAsString(request))
			.contentType(MediaType.APPLICATION_JSON));

		// then
		perform.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.CREATE_TAG_SUCCESS.getMessage()))
			.andExpect(
				jsonPath("$.code").value(String.valueOf(SuccessCode.CREATE_TAG_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("로그인하지 않으면, 카테고리 생성은 불가능 합니다.")
	@Test
	void createTag2() throws Exception {
		// given
		TagRequest.CreateTag request = TagRequest.CreateTag.of("태그2");

		// when
		ResultActions perform = mockMvc.perform(post("/tag")
			.content(objectMapper.writeValueAsString(request))
			.contentType(MediaType.APPLICATION_JSON));

		// then
		perform.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.NEED_LOGIN.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.NEED_LOGIN.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@WithMockUser
	@DisplayName("특정 태그를 삭제합니다.")
	@Test
	void deleteTag1() throws Exception {
		// given
		String saveTagName = tagDataFactory.newTagList(1).getFirst().getName();

		// when
		ResultActions perform = mockMvc.perform(delete("/tag/{tagName}", saveTagName));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.DELETE_TAG_SUCCESS.getMessage()))
			.andExpect(
				jsonPath("$.code").value(String.valueOf(SuccessCode.DELETE_TAG_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("로그인하지 않으면, 태그 삭제는 불가능 합니다.")
	@Test
	void deleteTag2() throws Exception {
		// given
		String saveTagName = tagDataFactory.newTagList(2).getFirst().getName();

		// when
		ResultActions perform = mockMvc.perform(delete("/tag/{tagName}", saveTagName));

		// then
		perform.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.NEED_LOGIN.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.NEED_LOGIN.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("태그 키워드 검색")
	@Test
	void searchTag1() throws Exception {
		// given
		tagDataFactory.newTagList(10);

		// when
		ResultActions perform = mockMvc.perform(get("/tag").queryParam("keyword", "태그1"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(2));
	}

	@DisplayName("태그 키워드 검색은, keyword param 은 필수값이 아닙니다.")
	@Test
	void searchTag2() throws Exception {
		// given
		tagDataFactory.newTagList(10);

		// when
		ResultActions perform = mockMvc.perform(get("/tag"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(10));
	}
}