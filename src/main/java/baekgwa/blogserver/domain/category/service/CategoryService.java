package baekgwa.blogserver.domain.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.category.dto.CategoryRequest;
import baekgwa.blogserver.domain.category.dto.CategoryResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.category.service
 * FileName    : CategoryService
 * Author      : Baekgwa
 * Date        : 2025-06-06
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-06     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;

	@Transactional
	public void create(CategoryRequest.CreateCategory createCategory) {
		// 1. 중복 검증
		if (categoryRepository.existsByName(createCategory.getName())) {
			throw new GlobalException(ErrorCode.DUPLICATION_CATEGORY);
		}

		// 2. Entity 생성 및 저장
		CategoryEntity categoryEntity = CategoryEntity.of(createCategory.getName());
		categoryRepository.save(categoryEntity);
	}

	@Transactional(readOnly = true)
	public List<CategoryResponse.CategoryList> getCategoryList() {
		// 1. category 이름순 기준으로 조회
		List<CategoryEntity> findCategoryList = categoryRepository.findALlByOrderByName();

		// 2. 응답 객체 변환 후, return
		return CategoryResponse.CategoryList.from(findCategoryList);
	}

	@Transactional
	public void deleteCategory(String categoryName) {
		long deleteCount = categoryRepository.deleteByName(categoryName);

		if(deleteCount <= 0) {
			throw new GlobalException(ErrorCode.NOT_EXIST_CATEGORY);
		}

		//todo : 이미 글에 카테고리가 할당된 경우, 어떻게 처리할지 고민 필요.
	}
}
