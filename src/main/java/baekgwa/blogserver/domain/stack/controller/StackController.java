package baekgwa.blogserver.domain.stack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.blogserver.domain.stack.dto.StackRequest;
import baekgwa.blogserver.domain.stack.dto.StackResponse;
import baekgwa.blogserver.domain.stack.service.StackService;
import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.stack.controller
 * FileName    : StackController
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/stack")
@Tag(name = "Stack Controller", description = "스택(시리즈) 관련")
public class StackController {

	private final StackService stackService;

	@PostMapping
	@Operation(summary = "신규 스택 등록")
	public BaseResponse<Void> createNewStack(
		@Valid @RequestBody StackRequest.NewStackSeries request
	) {
		stackService.createNewStackSeries(request);
		return BaseResponse.success(SuccessCode.CREATE_STACK_SUCCESS);
	}

	@GetMapping("/post/{postId}")
	@Operation(summary = "포스트와 관련된 스택 목록 반환")
	public BaseResponse<StackResponse.StackInfo> getRelativeStackPostInfo(
		@PathVariable(value = "postId") Long postId
	) {
		StackResponse.StackInfo response = stackService.getRelativeStackPostInfo(postId);
		return BaseResponse.success(SuccessCode.FIND_RELATIVE_STACK_SUCCESS, response);
	}
}
