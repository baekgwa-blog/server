package baekgwa.blogserver.domain.post.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.post.dto.PostRequest;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.domain.post.controller
 * FileName    : PostControllerTest
 * Author      : Baekgwa
 * Date        : 2025-06-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-20     Baekgwa               Initial creation
 */
@Transactional
class PostControllerTest extends SpringBootTestSupporter {

	@WithMockUser
	@DisplayName("포스팅 글 작성")
	@Test
	void createPost1() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(5);
		List<Long> saveTagIdList = saveTagList.stream().map(TagEntity::getId).toList();
		PostRequest.CreatePost request = PostRequest.CreatePost.of("제목", "내용", "설명", "썸네일url", saveTagIdList, saveCategory.getId());

		// when
		ResultActions perform = mockMvc.perform(post("/post")
			.content(objectMapper.writeValueAsString(request))
			.contentType(MediaType.APPLICATION_JSON));

		// then
		perform.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.CREATE_POST_SUCCESS.getMessage()))
			.andExpect(
				jsonPath("$.code").value(String.valueOf(SuccessCode.CREATE_POST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data.slug").isNotEmpty());
	}

	@DisplayName("로그인 한 회원만 포스팅이 가능")
	@Test
	void createPost2() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(5);
		List<Long> saveTagIdList = saveTagList.stream().map(TagEntity::getId).toList();
		PostRequest.CreatePost request = PostRequest.CreatePost.of("제목", "내용", "설명", "썸네일url", saveTagIdList, saveCategory.getId());

		// when
		ResultActions perform = mockMvc.perform(post("/post/detail")
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

	@DisplayName("포스팅 상세 조회")
	@Test
	void searchPost1() throws Exception {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		String savePostSlug = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst().getSlug();

		// when
		ResultActions perform = mockMvc.perform(get("/post/detail")
			.param("slug", savePostSlug));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data.title").isNotEmpty())
			.andExpect(jsonPath("$.data.content").isNotEmpty())
			.andExpect(jsonPath("$.data.thumbnailImage").isNotEmpty())
			.andExpect(jsonPath("$.data.slug").isNotEmpty())
			.andExpect(jsonPath("$.data.tagList").isArray())
			.andExpect(jsonPath("$.data.category").isNotEmpty());
	}

	@DisplayName("포스팅 목록 조회 및 검색")
	@Test
	void searchPostList1() throws Exception {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(10, saveTagList, saveCategory);

		// when
		ResultActions perform = mockMvc.perform(get("/post"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].title").isNotEmpty())
			.andExpect(jsonPath("$.data.content[0].description").isNotEmpty())
			.andExpect(jsonPath("$.data.content[0].thumbnailImage").isNotEmpty())
			.andExpect(jsonPath("$.data.content[0].slug").isNotEmpty())
			.andExpect(jsonPath("$.data.content[0].viewCount").isNumber())
			.andExpect(jsonPath("$.data.content[0].tagList").isArray())
			.andExpect(jsonPath("$.data.content[0].category").value(saveCategory.getName()))
			.andExpect(jsonPath("$.data.content[0].createdAt").isNotEmpty())
			.andExpect(jsonPath("$.data.content[0].modifiedAt").isNotEmpty());
	}

	@DisplayName("포스팅 목록 조회 및 검색은, 키워드, 페이지네이션, 정렬이 사용 가능합니다.")
	@Test
	void searchPostList2() throws Exception {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(10, saveTagList, saveCategory);

		// when
		ResultActions perform = mockMvc.perform(get("/post")
			.param("keyword", "제목")
			.param("page", "1")
			.param("size", "2")
			.param("category", saveCategory.getName())
			.param("sort", "OLDEST"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.REQUEST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.REQUEST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content.length()").value(2));
	}

	@WithMockUser
	@DisplayName("포스트 글을 삭제합니다.")
	@Test
	void deletePost1() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();

		// when
		ResultActions perform = mockMvc.perform(delete("/post/{postId}", savePost.getId()));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.DELETE_POST_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.DELETE_POST_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@DisplayName("로그인 한 회원만 포스트 글을 삭제할 수 있습니다.")
	@Test
	void deletePost2() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();

		// when
		ResultActions perform = mockMvc.perform(delete("/post/{postId}", savePost.getId()));

		// then
		perform.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.NEED_LOGIN.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.NEED_LOGIN.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}
}