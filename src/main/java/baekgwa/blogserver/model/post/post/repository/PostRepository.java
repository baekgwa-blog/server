package baekgwa.blogserver.model.post.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;

/**
 * PackageName : baekgwa.blogserver.model.post.post.repository
 * FileName    : PostRepository
 * Author      : Baekgwa
 * Date        : 2025-06-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-19     Baekgwa               Initial creation
 */
public interface PostRepository extends JpaRepository<PostEntity, Long>, PostRepositoryCustom {
	boolean existsByTitle(String title);

	boolean existsByCategory(CategoryEntity category);

	@EntityGraph(attributePaths = {"category"})
	Optional<PostEntity> findBySlug(String slug);
}
