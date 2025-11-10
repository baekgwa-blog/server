package baekgwa.blogserver.model.embedding.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.model.embedding.entity
 * FileName    : EmbeddingJob
 * Author      : Baekgwa
 * Date        : 25. 11. 10.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 10.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum EmbeddingJob {

	CREATE("임베딩 생성"),
	DELETE("임베딩 삭제");

	private final String description;
}
