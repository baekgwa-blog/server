package baekgwa.blogserver.integration.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.integration.factory
 * FileName    : CategoryDataFactory
 * Author      : Baekgwa
 * Date        : 2025-06-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-09     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class CategoryDataFactory {

	private final CategoryRepository categoryRepository;
	private final EntityManager em;

	@Transactional
	public List<CategoryEntity> newCategoryList(final long count) {

		if (count <= 0) {
			throw new IllegalArgumentException("1개 이상 입력되어야 합니다.");
		}

		List<CategoryEntity> newCategoryList = new ArrayList<>();
		for (int index = 1; index <= count; index++) {
			CategoryEntity newCategory = CategoryEntity.of(String.format("%s%d", "카테고리", index));
			newCategoryList.add(newCategory);
		}

		List<CategoryEntity> saveCategory = categoryRepository.saveAll(newCategoryList);

		em.flush();
		em.clear();

		return saveCategory;
	}
}
