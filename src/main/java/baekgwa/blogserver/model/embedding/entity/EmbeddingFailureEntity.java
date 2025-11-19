package baekgwa.blogserver.model.embedding.entity;

import baekgwa.blogserver.global.entity.TemporalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.model.embedding
 * FileName    : EmbeddingFailureEntity
 * Author      : Baekgwa
 * Date        : 25. 11. 8.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 8.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "embedding_failure")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddingFailureEntity extends TemporalEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "post_id", unique = true, nullable = false)
	private Long postId;

	@Enumerated(EnumType.STRING)
	@Column(name = "embedding_job", nullable = false)
	private EmbeddingJob embeddingJob;

	@Column(name = "reason", nullable = false)
	private String reason;

	public EmbeddingFailureEntity(Long postId, EmbeddingJob embeddingJob, String reason) {
		this.postId = postId;
		this.embeddingJob = embeddingJob;
		this.reason = reason;
	}

	public static EmbeddingFailureEntity of(Long postId, String reason, EmbeddingJob embeddingJob) {
		return new EmbeddingFailureEntity(postId, embeddingJob, reason);
	}

	public void updateReason(String reason) {
		this.reason = reason;
	}
}
