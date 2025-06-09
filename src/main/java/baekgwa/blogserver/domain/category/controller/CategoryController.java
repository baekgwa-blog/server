package baekgwa.blogserver.domain.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.blogserver.domain.category.dto.CategoryRequest;
import baekgwa.blogserver.domain.category.dto.CategoryResponse;
import baekgwa.blogserver.domain.category.service.CategoryService;
import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.category.controller
 * FileName    : CategoryController
 * Author      : Baekgwa
 * Date        : 2025-06-06
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-06     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Tag(name = "Category Controller", description = "카테고리 관련")
public class CategoryController {
	private final CategoryService categoryService;

	@PostMapping
	@Operation(summary = "카테고리 생성")
	public BaseResponse<Void> createCategory(
		@Valid @RequestBody CategoryRequest.CreateCategory request)
	{
		categoryService.create(request);
		return BaseResponse.success(SuccessCode.CREATE_CATEGORY_SUCCESS);
	}

	@GetMapping
	@Operation(summary = "카테고리 전체 목록 조회")
	public BaseResponse<List<CategoryResponse.CategoryList>> getCategoryList() {
		List<CategoryResponse.CategoryList> findCategoryList = categoryService.getCategoryList();

		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS, findCategoryList);
	}

	@DeleteMapping("/{categoryName}")
	@Operation(summary = "카테고리 삭제")
	public BaseResponse<Void> deleteCategory(
		@PathVariable(value = "categoryName", required = true) String categoryName
	) {
		categoryService.deleteCategory(categoryName);

		return BaseResponse.success(SuccessCode.DELETE_CATEGORY_SUCCESS);
	}
}
