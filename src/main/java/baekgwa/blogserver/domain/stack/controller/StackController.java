package baekgwa.blogserver.domain.stack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	public BaseResponse<StackResponse.CreateNewStack> createNewStack(
		@Valid @RequestBody StackRequest.NewStackSeries request
	) {
		StackResponse.CreateNewStack response = stackService.createNewStackSeries(request);
		return BaseResponse.success(SuccessCode.CREATE_STACK_SUCCESS, response);
	}

	@PutMapping("{stackId}")
	@Operation(summary = "스택 수정")
	public BaseResponse<StackResponse.ModifyStack> modifyStack(
		@PathVariable(value = "stackId") Long stackId,
		@Valid @RequestBody StackRequest.ModifyStackSeries request
	) {
		StackResponse.ModifyStack response = stackService.modifyStackSeries(stackId, request);
		return BaseResponse.success(SuccessCode.MODIFY_STACK_SUCCESS, response);
	}

	@GetMapping("/modify/{stackId}")
	@Operation(summary = "수정용 스택 정보 조회")
	public BaseResponse<StackResponse.ModifyStackInfo> getModifyStackInfo(
		@PathVariable(value = "stackId") Long stackId
	) {
		StackResponse.ModifyStackInfo response = stackService.getModifyStackInfo(stackId);
		return BaseResponse.success(SuccessCode.GET_MODIFY_STACK_INFO_SUCCESS, response);
	}

	@GetMapping
	@Operation(summary = "스택 목록 조회")
	public BaseResponse<List<StackResponse.StackDetailInfo>> getAllStack() {
		List<StackResponse.StackDetailInfo> response = stackService.getAllStack();
		return BaseResponse.success(SuccessCode.GET_ALL_STACK_SUCCESS, response);
	}

	@GetMapping("/post/{postId}")
	@Operation(summary = "포스트와 관련된 스택 목록 반환")
	public BaseResponse<StackResponse.StackInfo> getRelativeStackPostInfo(
		@PathVariable(value = "postId") Long postId
	) {
		StackResponse.StackInfo response = stackService.getRelativeStackPostInfo(postId);
		return BaseResponse.success(SuccessCode.FIND_RELATIVE_STACK_SUCCESS, response);
	}

	@GetMapping("/{stackId}")
	@Operation(summary = "스택 상세 조회")
	public BaseResponse<StackResponse.StackDetail> getStackDetail(
		@PathVariable(value = "stackId") Long stackId
	) {
		StackResponse.StackDetail response = stackService.getStackDetail(stackId);
		return BaseResponse.success(SuccessCode.GET_STACK_DETAIL_SUCCESS, response);
	}
}
