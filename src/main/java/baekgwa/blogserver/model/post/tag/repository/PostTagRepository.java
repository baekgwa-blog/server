package baekgwa.blogserver.model.post.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.tag.entity.PostTagEntity;

/**
 * PackageName : baekgwa.blogserver.model.post.tag.repository
 * FileName    : PostTagRepository
 * Author      : Baekgwa
 * Date        : 2025-06-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-19     Baekgwa               Initial creation
 */
public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {

	@EntityGraph(attributePaths = {"tag"})
	List<PostTagEntity> findAllByPost(PostEntity post);
}
