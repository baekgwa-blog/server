package baekgwa.blogserver.model.stack.stack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import baekgwa.blogserver.model.stack.stack.dto.StackStatsDto;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;

/**
 * PackageName : baekgwa.blogserver.model.stack.stack.repository
 * FileName    : StackRepository
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
public interface StackRepository extends JpaRepository<StackEntity, Long> {

	boolean existsByTitle(String title);

	@Query("SELECT s AS stack, COUNT(p) AS postCount, MAX(p.modifiedAt) AS lastUpdate " +
		"FROM StackEntity s " +
		"LEFT JOIN FETCH s.category " +
		"LEFT JOIN StackPostEntity sp ON sp.stack = s " +
		"LEFT JOIN sp.post p " +
		"GROUP BY s.id")
	List<StackStatsDto> findStackStats();

	@EntityGraph(attributePaths = {"category"})
	Optional<StackEntity> findWithCategoryById(Long id);
}
