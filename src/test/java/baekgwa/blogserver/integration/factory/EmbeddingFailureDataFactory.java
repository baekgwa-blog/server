package baekgwa.blogserver.integration.factory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingJob;
import baekgwa.blogserver.model.embedding.repository.EmbeddingFailureRepository;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.integration.factory
 * FileName    : EmbeddingFailureDataFactory
 * Author      : Baekgwa
 * Date        : 25. 11. 17.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 17.     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class EmbeddingFailureDataFactory {

	private final EmbeddingFailureRepository embeddingFailureRepository;
	private final EntityManager em;

	@Transactional
	public EmbeddingFailureEntity newFailureList(PostEntity post, String reason, EmbeddingJob embeddingJob) {
		EmbeddingFailureEntity failure = EmbeddingFailureEntity.of(post.getId(), reason, embeddingJob);
		EmbeddingFailureEntity savedFailure = embeddingFailureRepository.save(failure);

		em.flush();
		em.clear();

		return savedFailure;
	}
}
