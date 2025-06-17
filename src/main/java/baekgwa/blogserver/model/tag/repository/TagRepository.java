package baekgwa.blogserver.model.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.model.tag.repository
 * FileName    : TagRepository
 * Author      : Baekgwa
 * Date        : 2025-06-16
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-16     Baekgwa               Initial creation
 */
public interface TagRepository extends JpaRepository<TagEntity, Long> {

	boolean existsByName(String name);

	long deleteByName(String name);

	List<TagEntity> findByNameContaining(String name);
}
