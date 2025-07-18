package baekgwa.blogserver.model.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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

	List<CategoryEntity> findALlByOrderByName();

	Optional<CategoryEntity> findByName(String name);
}
