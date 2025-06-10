package baekgwa.blogserver.domain.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.category.dto.CategoryRequest;
import baekgwa.blogserver.domain.category.dto.CategoryResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;

/**
 * PackageName : baekgwa.blogserver.domain.category.service
 * FileName    : CategoryServiceTest
 * Author      : Baekgwa
 * Date        : 2025-06-10
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-10     Baekgwa               Initial creation
 */
@Transactional
class CategoryServiceTest extends SpringBootTestSupporter {

	@DisplayName("새로운 카테고리를 생성합니다.")
	@Test
	void create1() {
		// given
		CategoryRequest.CreateCategory request = CategoryRequest.CreateCategory.of("카테고리1");

		// when
		categoryService.create(request);

		// then
		CategoryEntity findCategory = categoryRepository.findAll().getFirst();
		assertThat(findCategory.getName()).isEqualTo("카테고리1");
	}

	@DisplayName("중복된 카테고리는 생성되지 않습니다.")
	@Test
	void create2() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		CategoryRequest.CreateCategory request = CategoryRequest.CreateCategory.of(saveCategory.getName());

		// when // then
		assertThatThrownBy(() -> categoryService.create(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DUPLICATION_CATEGORY);
	}

	@DisplayName("모든 카테고리를 조회합니다.")
	@Test
	void getCategoryList1() {
		// given
		categoryDataFactory.newCategoryList(10);

		// when
		List<CategoryResponse.CategoryList> categoryList = categoryService.getCategoryList();

		// then
		assertThat(categoryList).hasSize(10);
	}

	@DisplayName("특정 카테고리를 삭제합니다")
	@Test
	void deleteCategory1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(10).getFirst();

		// when
		categoryService.deleteCategory(saveCategory.getName());

		// then
		List<CategoryEntity> findCategoryList = categoryRepository.findAll();
		assertThat(findCategoryList).hasSize(9);
	}

	@DisplayName("없는 카테고리를 삭제하려고 하면 실패합니다.")
	@Test
	void deleteCategory2() {
		// given

		// when

		// then
		assertThatThrownBy(() -> categoryService.deleteCategory("없는 카테고리"))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_CATEGORY);
	}
}