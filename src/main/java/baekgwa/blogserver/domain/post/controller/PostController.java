package baekgwa.blogserver.domain.post.controller;

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
import baekgwa.blogserver.domain.post.service.PostService;
import baekgwa.blogserver.domain.post.type.PostListSort;
import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.PageResponse;
import baekgwa.blogserver.global.response.SuccessCode;
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
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post Controller", description = "포스팅 관련")
public class PostController {

	private final PostService postService;

	@PostMapping
	@Operation(summary = "글 포스팅")
	public BaseResponse<PostResponse.CreatePostResponse> createPost(
		@Valid @RequestBody PostRequest.CreatePost request
	) {
		PostResponse.CreatePostResponse response = postService.create(request);

		return BaseResponse.success(SuccessCode.CREATE_POST_SUCCESS, response);
	}

	@GetMapping("/detail")
	@Operation(summary = "포스트 상세 조회")
	public BaseResponse<PostResponse.GetPostDetailResponse> searchPost(
		@RequestParam(value = "slug", required = true) String slug,
		HttpServletRequest request
	) {
		PostResponse.GetPostDetailResponse response = postService.getPostDetail(slug, request);
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, response);
	}

	@GetMapping
	@Operation(summary = "포스트 리스트 검색")
	public BaseResponse<PageResponse<PostResponse.GetPostResponse>> searchPostList(
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "page", required = false, defaultValue = "0") final int page,
		@RequestParam(value = "size", required = false, defaultValue = "3") final int size,
		@RequestParam(value = "category", required = false) final String category,
		@RequestParam(value = "sort", required = false, defaultValue = "LATEST") final PostListSort sort
	) {
		PageResponse<PostResponse.GetPostResponse> response = postService.getPostList(keyword, page, size, category,
			sort);
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, response);
	}

	@DeleteMapping("/{postId}")
	@Operation(summary = "포스트 글 삭제")
	public BaseResponse<Void> deletePost(
		@PathVariable(value = "postId", required = true) final Long postId
	) {
		postService.deletePost(postId);
		return BaseResponse.success(SuccessCode.DELETE_POST_SUCCESS);
	}
}
