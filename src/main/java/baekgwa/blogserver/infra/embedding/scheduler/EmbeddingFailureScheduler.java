package baekgwa.blogserver.infra.embedding.scheduler;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import baekgwa.blogserver.infra.embedding.service.EmbeddingFailureService;
import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.repository.EmbeddingFailureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.scheduler
 * FileName    : EmbeddingFailureScheduler
 * Author      : Baekgwa
 * Date        : 25. 11. 15.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 15.     Baekgwa               Initial creation
 */
@Profile("!test")
@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingFailureScheduler {

	private final EmbeddingFailureRepository failureRepository;
	private final EmbeddingFailureService failureService;

	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void retryEmbeddingFailures() {
		List<EmbeddingFailureEntity> failureList = failureRepository.findAll();
		for (EmbeddingFailureEntity failure : failureList) {
			try {
				failureService.processSingleFailure(failure);
				log.debug("Successfully retry embedding postId={}", failure.getPostId());
			} catch (Exception e) {
				log.warn("Retry embedding failed id={}: {}", failure.getId(), e.getMessage());
			}
		}
	}
}
