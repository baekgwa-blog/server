package baekgwa.blogserver.domain.category.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.category.dto.CategoryRequest;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;

/**
 * PackageName : baekgwa.blogserver.domain.category.controller
 * FileName    : CategoryControllerTest
 * Author      : Baekgwa
 * Date        : 2025-06-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-09     Baekgwa               Initial creation
 */
@Transactional
class CategoryControllerTest extends SpringBootTestSupporter {

	@WithMockUser
	@DisplayName("새로운 카테고리를 생성합니다.")
	@Test
	void createCategory1() throws Exception {
		// given
		CategoryRequest.CreateCategory request = CategoryRequest.CreateCategory.of("새로운 카테고리");

		// when
		ResultActions perform = mockMvc.perform(post("/category")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		perform.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.CREATE_CATEGORY_SUCCESS.getMessage()))
			.andExpect(
				jsonPath("$.code").value(String.valueOf(SuccessCode.CREATE_CATEGORY_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("로그인 하지 않으면, 카테고리 생성이 불가능합니다.")
	@Test
	void createCategory2() throws Exception {
		// given
		CategoryRequest.CreateCategory request = CategoryRequest.CreateCategory.of("새로운 카테고리");

		// when
		ResultActions perform = mockMvc.perform(post("/category")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		perform.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.NEED_LOGIN.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.NEED_LOGIN.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@WithMockUser
	@DisplayName("이미 중복된 카테고리는 생성이 불가능하다.")
	@Test
	void createCategory3() throws Exception {
		// given
		categoryDataFactory.newCategoryList(1);
		CategoryRequest.CreateCategory request = CategoryRequest.CreateCategory.of("카테고리1");

		// when
		ResultActions perform = mockMvc.perform(post("/category")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		perform.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATION_CATEGORY.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATION_CATEGORY.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("카테고리 목록을 전체 조회합니다.")
	@Test
	void getCategoryList1() throws Exception {
		// given
		categoryDataFactory.newCategoryList(10);

		// when
		ResultActions perform = mockMvc.perform(get("/category"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].name").exists())
			.andExpect(jsonPath("$.data[0].id").exists())
			.andExpect(jsonPath("$.data[0].count").exists());
	}

	@WithMockUser
	@DisplayName("특정 카테고리를 삭제 합니다.")
	@Test
	void deleteCategory1() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();

		// when
		ResultActions perform = mockMvc.perform(delete("/category/{categoryName}", saveCategory.getName()));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.DELETE_CATEGORY_SUCCESS.getMessage()))
			.andExpect(
				jsonPath("$.code").value(String.valueOf(SuccessCode.DELETE_CATEGORY_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("카테고리 삭제는, 로그인 한 상태에서만 가능합니다.")
	@Test
	void deleteCategory2() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();

		// when
		ResultActions perform = mockMvc.perform(delete("/category/{categoryName}", saveCategory.getName()));

		// then
		perform.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.NEED_LOGIN.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.NEED_LOGIN.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}
}