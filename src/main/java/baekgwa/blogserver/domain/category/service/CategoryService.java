package baekgwa.blogserver.domain.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.category.dto.CategoryRequest;
import baekgwa.blogserver.domain.category.dto.CategoryResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.category.projection.CategoryPostCount;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
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
	private final PostRepository postRepository;

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

		List<CategoryPostCount> findCategoryList = categoryRepository.findAllWithPostCount();

		// 2. 응답 객체 변환 후, return
		return CategoryResponse.CategoryList.from(findCategoryList);
	}

	@Transactional
	public void deleteCategory(String categoryName) {
		// 1. Category Entity 조회
		CategoryEntity findCategory = categoryRepository.findByName(categoryName)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_CATEGORY));

		// 2. 해당 카테고리로 연결된 글이 있는지 확인
		//		연결된 글이 있으면, 삭제 불가능.
		if (postRepository.existsByCategory(findCategory)) {
			throw new GlobalException(ErrorCode.REGISTERED_CATEGORY_POST);
		}

		// 3. entity 삭제
		categoryRepository.delete(findCategory);
	}
}
