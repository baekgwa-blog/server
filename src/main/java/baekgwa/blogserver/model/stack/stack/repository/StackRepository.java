package baekgwa.blogserver.model.stack.stack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
