package baekgwa.blogserver.domain.category.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.category.dto.CategoryRequest;
import baekgwa.blogserver.domain.category.dto.CategoryResponse;
import baekgwa.blogserver.global.cache.CacheType;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.category.projection.CategoryPostCount;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final PostRepository postRepository;

	@CacheEvict(cacheNames = CacheType.CacheNames.CATEGORY_LIST, allEntries = true)
	@Transactional
	public void create(CategoryRequest.CreateCategory createCategory) {
		if (categoryRepository.existsByName(createCategory.getName())) {
			throw new GlobalException(ErrorCode.DUPLICATION_CATEGORY);
		}

		CategoryEntity categoryEntity = CategoryEntity.of(createCategory.getName());
		categoryRepository.save(categoryEntity);
	}

	@Cacheable(
		cacheNames = CacheType.CacheNames.CATEGORY_LIST,
		key = "@cacheKeyFactory.getCategoryListKey()",
		unless = "#result == null"
	)
	@Transactional(readOnly = true)
	public List<CategoryResponse.CategoryList> getCategoryList() {
		log.debug("[Cache Miss] Get Category List");
		List<CategoryPostCount> findCategoryList = categoryRepository.findAllWithPostCount();
		return CategoryResponse.CategoryList.from(findCategoryList);
	}

	@CacheEvict(cacheNames = CacheType.CacheNames.CATEGORY_LIST, allEntries = true)
	@Transactional
	public void deleteCategory(String categoryName) {
		CategoryEntity findCategory = categoryRepository.findByName(categoryName)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_CATEGORY));

		if (postRepository.existsByCategory(findCategory)) {
			throw new GlobalException(ErrorCode.REGISTERED_CATEGORY_POST);
		}

		categoryRepository.delete(findCategory);
	}
}
