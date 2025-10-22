package baekgwa.blogserver.model.stack.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;

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
	@Query("SELECT sp FROM StackPostEntity sp WHERE sp.post.id = :postId")
	Optional<StackPostEntity> findByPostId(@Param(value = "postId") Long postId);

	@EntityGraph(attributePaths = {"post"})
	List<StackPostEntity> findAllByStack(StackEntity findStack);

	@Query("SELECT COUNT(sp) > 0 FROM StackPostEntity sp WHERE sp.post.id IN :postIdList")
	boolean existsByPostIdIn(@Param("postIdList") List<Long> postIdList);
}
