package baekgwa.blogserver.model.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import baekgwa.blogserver.model.category.entity.CategoryEntity;

/**
 * PackageName : baekgwa.blogserver.model.category.repository
 * FileName    : CategoryRepository
 * Author      : Baekgwa
 * Date        : 2025-06-06
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-06     Baekgwa               Initial creation
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

	boolean existsByName(String name);

	Optional<CategoryEntity> findByName(String name);

	record CategoryPostCount(CategoryEntity category, Long postCount) {}
	@Query("SELECT new baekgwa.blogserver.model.category.repository.CategoryRepository$CategoryPostCount(c, COUNT(p.id)) " +
		"FROM CategoryEntity c LEFT JOIN PostEntity p ON c.id = p.category.id " +
		"GROUP BY c.id " +
		"ORDER BY c.name")
	List<CategoryPostCount> findAllWithPostCount();
}
