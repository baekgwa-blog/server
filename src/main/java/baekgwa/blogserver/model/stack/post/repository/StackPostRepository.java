package baekgwa.blogserver.model.stack.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;

/**
 * PackageName : baekgwa.blogserver.model.stack.post.repository
 * FileName    : StackPostRepository
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
public interface StackPostRepository extends JpaRepository<StackPostEntity, Long> {
}
