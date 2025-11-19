package baekgwa.blogserver.model.embedding.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.integration.SpringBootTestSupporter;

/**
 * PackageName : baekgwa.blogserver.model.embedding.entity
 * FileName    : EmbeddingFailureEntityTest
 * Author      : Baekgwa
 * Date        : 25. 11. 17.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 17.     Baekgwa               Initial creation
 */
@Transactional
class EmbeddingFailureEntityTest extends SpringBootTestSupporter {

	@DisplayName("사유를 업데이트 합니다.")
	@Test
	void updateReason1() {
		// given
		EmbeddingFailureEntity failure = EmbeddingFailureEntity.of(1L, "변경 전", EmbeddingJob.CREATE);
		embeddingFailureRepository.save(failure);

		// when
		failure.updateReason("변경 후");

		// then
		EmbeddingFailureEntity findFailure = embeddingFailureRepository.findAll().getFirst();
		Assertions.assertThat(findFailure.getReason()).isEqualTo("변경 후");
	}
}