package baekgwa.blogserver.domain.stack.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.stack.dto.StackRequest;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.domain.stack.controller
 * FileName    : StackControllerTest
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@Transactional
class StackControllerTest extends SpringBootTestSupporter {

	@WithMockUser
	@DisplayName("신규 스택 등록")
	@Test
	void createNewStack1() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet())).toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList, "imageUrl");

		// when
		ResultActions perform = mockMvc.perform(post("/stack")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		perform.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.CREATE_STACK_SUCCESS.getMessage()))
			.andExpect(
				jsonPath("$.code").value(String.valueOf(SuccessCode.CREATE_STACK_SUCCESS.getStatus().value())));
	}

	@DisplayName("신규 스택 등록은 로그인 한 회원만 가능합니다.")
	@Test
	void createNewStack2() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);

		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet())).toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList, "imageUrl");

		// when
		ResultActions perform = mockMvc.perform(post("/stack")
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

	@DisplayName("포스트와 관련된 스택 목록 확인 API")
	@Test
	void getRelativeStackPostInfo1() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		// when
		PostEntity savePost = savePostList.getFirst();
		ResultActions perform = mockMvc.perform(get("/stack/post/{postId}", savePost.getId()));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.FIND_RELATIVE_STACK_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.FIND_RELATIVE_STACK_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data.stackId").isNumber())
			.andExpect(jsonPath("$.data.title").isNotEmpty())
			.andExpect(jsonPath("$.data.stackPostInfoList").isArray())
			.andExpect(jsonPath("$.data.stackPostInfoList.length()").value(2))
			.andExpect(jsonPath("$.data.stackPostInfoList[0].postId").isNumber())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].title").isNotEmpty())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].slug").isNotEmpty())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].sequence").isNumber());
	}

	@DisplayName("저장된 모든 Stack 정보를 조회합니다.")
	@Test
	void getAllStack1() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(4, saveTagList, saveCategory);
		List<StackEntity> saveStackList = stackDataFactory.newStack(2, saveCategory);
		stackDataFactory.newStackPost(saveStackList.getFirst(), List.of(savePostList.getFirst(), savePostList.get(1)));
		stackDataFactory.newStackPost(saveStackList.getLast(), List.of(savePostList.get(2), savePostList.getLast()));

		// when
		ResultActions perform = mockMvc.perform(get("/stack"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.GET_ALL_STACK_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.GET_ALL_STACK_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].stackId").isNumber())
			.andExpect(jsonPath("$.data[0].title").isNotEmpty())
			.andExpect(jsonPath("$.data[0].description").isNotEmpty())
			.andExpect(jsonPath("$.data[0].category").isNotEmpty())
			.andExpect(jsonPath("$.data[0].updatedAt").isNotEmpty())
			.andExpect(jsonPath("$.data[0].count").isNumber());
	}

	@DisplayName("특정 스택의 정보와 할당된 포스트들을 조회합니다.")
	@Test
	void getStackDetail() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		// when
		ResultActions perform = mockMvc.perform(get("/stack/{stackId}", saveStack.getId()));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.GET_STACK_DETAIL_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.GET_STACK_DETAIL_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data.stackId").isNumber())
			.andExpect(jsonPath("$.data.title").isNotEmpty())
			.andExpect(jsonPath("$.data.description").isNotEmpty())
			.andExpect(jsonPath("$.data.category").isNotEmpty())
			.andExpect(jsonPath("$.data.stackPostInfoList").isArray())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].postId").isNumber())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].title").isNotEmpty())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].description").isNotEmpty())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].slug").isNotEmpty())
			.andExpect(jsonPath("$.data.stackPostInfoList[0].viewCount").isNumber())
		;
	}
}
