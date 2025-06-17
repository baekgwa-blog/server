package baekgwa.blogserver.domain.tag.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.blogserver.domain.tag.dto.TagRequest;
import baekgwa.blogserver.domain.tag.dto.TagResponse;
import baekgwa.blogserver.domain.tag.service.TagService;
import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.tag.controller
 * FileName    : TagController
 * Author      : Baekgwa
 * Date        : 2025-06-16
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-16     Baekgwa               Initial creation
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
@Tag(name = "Tag Controller", description = "태그 관련")
public class TagController {
	private final TagService tagService;

	@PostMapping
	@Operation(summary = "태그 생성")
	public BaseResponse<Void> createTag(
		@Valid @RequestBody TagRequest.CreateTag request) {
		tagService.createTag(request);
		return BaseResponse.success(SuccessCode.CREATE_TAG_SUCCESS);
	}

	@DeleteMapping("/{tagName}")
	@Operation(summary = "태그 삭제")
	public BaseResponse<Void> deleteTag(
		@PathVariable(value = "tagName", required = true) String tagName
	) {
		tagService.deleteTag(tagName);

		return BaseResponse.success(SuccessCode.DELETE_TAG_SUCCESS);
	}

	@GetMapping
	@Operation(summary = "태그 키워드 검색")
	public BaseResponse<List<TagResponse.TagList>> searchTag(
		@RequestParam(value = "keyword", required = false) String keyword
	) {
		List<TagResponse.TagList> searchTagList = tagService.searchTag(keyword);

		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, searchTagList);
	}
}
