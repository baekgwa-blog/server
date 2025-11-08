package baekgwa.blogserver.model.embedding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;

/**
 * PackageName : baekgwa.blogserver.model.embedding.repository
 * FileName    : EmbeddingFailureRepository
 * Author      : Baekgwa
 * Date        : 25. 11. 8.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 8.     Baekgwa               Initial creation
 */
public interface EmbeddingFailureRepository extends JpaRepository<EmbeddingFailureEntity, Long> {
}
