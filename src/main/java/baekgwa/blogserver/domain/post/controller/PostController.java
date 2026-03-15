package baekgwa.blogserver.domain.post.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.blogserver.domain.post.dto.PostRequest;
import baekgwa.blogserver.domain.post.dto.PostResponse;
import baekgwa.blogserver.domain.post.service.PostEventService;
import baekgwa.blogserver.domain.post.service.PostService;
import baekgwa.blogserver.domain.post.service.RecommendationService;
import baekgwa.blogserver.domain.post.type.PostListSort;
import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.PageResponse;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.global.util.ClientIpUtils;
import baekgwa.blogserver.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.post.controller
 * FileName    : PostController
 * Author      : Baekgwa
 * Date        : 2025-06-19
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-19     Baekgwa               Initial creation
 * 2026-03-15     Baekgwa               행동 이벤트 발행(UserBehaviorService), 추천 API 추가
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post Controller", description = "포스팅 관련")
public class PostController {

	private final PostService postService;
	private final RecommendationService recommendationService;
	private final PostEventService postEventService;

	@PostMapping
	@Operation(summary = "글 포스팅")
	public BaseResponse<PostResponse.CreatePostResponse> createPost(
		@Valid @RequestBody PostRequest.CreatePost request
	) {
		return BaseResponse.success(SuccessCode.CREATE_POST_SUCCESS, postService.create(request));
	}

	@GetMapping("/detail")
	@Operation(summary = "포스트 상세 조회")
	public BaseResponse<PostResponse.GetPostDetailResponse> searchPost(
		@RequestParam(value = "slug") String slug,
		HttpServletRequest request
	) {
		PostResponse.GetPostDetailResponse response = postService.getPostDetail(slug);

		CookieUtil.extractSessionId(request)
			.ifPresent(sid -> postEventService.publishPostViewed(sid, response.getId(), slug));

		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, response);
	}

	@PostMapping("/{slug}/view")
	@Operation(summary = "포스트 조회수 증가")
	public BaseResponse<Void> increaseViewCount(
		@PathVariable String slug,
		HttpServletRequest request
	) {
		postService.increaseViewCount(slug, ClientIpUtils.extract(request));
		return BaseResponse.success(SuccessCode.INCREASE_VIEW_COUNT_SUCCESS);
	}

	@GetMapping
	@Operation(summary = "포스트 리스트 검색")
	public BaseResponse<PageResponse<PostResponse.GetPostResponse>> searchPostList(
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "page", required = false, defaultValue = "0") final int page,
		@RequestParam(value = "size", required = false, defaultValue = "3") final int size,
		@RequestParam(value = "category", required = false) final String category,
		@RequestParam(value = "sort", required = false, defaultValue = "LATEST") final PostListSort sort,
		HttpServletRequest request
	) {
		PageResponse<PostResponse.GetPostResponse> response = postService.getPostList(keyword, page, size, category, sort);

		if (StringUtils.hasText(keyword)) {
			CookieUtil.extractSessionId(request)
				.ifPresent(sid -> postEventService.publishPostSearched(sid, keyword, category));
		}

		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, response);
	}

	@DeleteMapping("/{slug}")
	@Operation(summary = "포스트 글 삭제")
	public BaseResponse<Void> deletePost(
		@PathVariable(value = "slug") final String slug
	) {
		postService.deletePost(slug);
		return BaseResponse.success(SuccessCode.DELETE_POST_SUCCESS);
	}

	@GetMapping("/recommendation")
	@Operation(summary = "추천 포스트 조회")
	public BaseResponse<List<PostResponse.GetPostResponse>> getRecommendation(
		@RequestParam(value = "size", required = false, defaultValue = "5") final int size,
		HttpServletRequest request
	) {
		Optional<String> sid = CookieUtil.extractSessionId(request);
		if (sid.isEmpty()) {
			return BaseResponse.success(SuccessCode.GET_RECOMMENDATION_SUCCESS, Collections.emptyList());
		}
		return BaseResponse.success(SuccessCode.GET_RECOMMENDATION_SUCCESS,
			recommendationService.getRecommendations(sid.get(), size));
	}
}
