package baekgwa.blogserver.domain.post.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
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
import jakarta.servlet.http.Cookie;

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
 * 2026-03-15     Baekgwa               세션 쿠키, 행동 이벤트, 추천 API 테스트 추가
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
		PostRequest.CreatePost request = PostRequest.CreatePost.of("제목", "내용", "설명", "썸네일url", saveTagIdList,
			saveCategory.getId());

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
		PostRequest.CreatePost request = PostRequest.CreatePost.of("제목", "내용", "설명", "썸네일url", saveTagIdList,
			saveCategory.getId());

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

	@DisplayName("포스팅 상세 조회 - sid 쿠키가 없으면 새로 발급됩니다.")
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
			.andExpect(jsonPath("$.data.category").isNotEmpty())
			.andExpect(cookie().exists("sid"));
	}

	@DisplayName("포스팅 상세 조회 - sid 쿠키가 있으면 POST_VIEWED 행동 이벤트가 발행됩니다.")
	@Test
	void searchPost2() throws Exception {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		String savePostSlug = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst().getSlug();

		// when
		ResultActions perform = mockMvc.perform(get("/post/detail")
			.param("slug", savePostSlug)
			.cookie(new Cookie("sid", "existing-session-id")));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.data.title").isNotEmpty());
	}

	@DisplayName("포스트 조회수 증가 - 정상적으로 조회수가 증가합니다.")
	@Test
	void increaseViewCount1() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();

		// when
		ResultActions perform = mockMvc.perform(post("/post/{slug}/view", savePost.getSlug()));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.INCREASE_VIEW_COUNT_SUCCESS.getMessage()));
	}

	@DisplayName("포스트 조회수 증가 - sid 쿠키 유무와 관계없이 조회수가 증가합니다.")
	@Test
	void increaseViewCount2() throws Exception {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();

		// when
		ResultActions perform = mockMvc.perform(post("/post/{slug}/view", savePost.getSlug())
			.cookie(new Cookie("sid", "test-session-id")));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.INCREASE_VIEW_COUNT_SUCCESS.getMessage()));
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
			.andExpect(jsonPath("$.data.content.length()").value(3))
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

	@DisplayName("키워드 검색 시 sid 쿠키가 있으면 행동 이벤트가 발행됩니다.")
	@Test
	void searchPostList3() throws Exception {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(5, saveTagList, saveCategory);

		// when
		ResultActions perform = mockMvc.perform(get("/post")
			.param("keyword", "제목")
			.cookie(new Cookie("sid", "test-session-id")));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true));
	}

	@DisplayName("키워드가 없으면 행동 이벤트가 발행되지 않습니다.")
	@Test
	void searchPostList4() throws Exception {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(5, saveTagList, saveCategory);

		// when
		ResultActions perform = mockMvc.perform(get("/post")
			.cookie(new Cookie("sid", "test-session-id")));

		// then
		perform.andDo(print())
			.andExpect(status().isOk());
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
		ResultActions perform = mockMvc.perform(delete("/post/{slug}", savePost.getSlug()));

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

	@DisplayName("sid 쿠키가 없으면 추천 포스트 조회는 빈 리스트를 반환합니다.")
	@Test
	void getRecommendation1() throws Exception {
		// when
		ResultActions perform = mockMvc.perform(get("/post/recommendation"));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.GET_RECOMMENDATION_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(0));
	}

	@DisplayName("sid 쿠키가 있으면 RecommendationService 를 호출하여 추천 포스트를 반환합니다.")
	@Test
	void getRecommendation2() throws Exception {
		// given
		String sessionId = "test-session-uuid";
		given(recommendationService.getRecommendations(eq(sessionId), eq(5))).willReturn(Collections.emptyList());

		// when
		ResultActions perform = mockMvc.perform(get("/post/recommendation")
			.param("size", "5")
			.cookie(new Cookie("sid", sessionId)));

		// then
		perform.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.GET_RECOMMENDATION_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.data").isArray());
	}
}
