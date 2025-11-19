package baekgwa.blogserver.domain.ai.dto;

import java.util.List;

/**
 * PackageName : baekgwa.blogserver.domain.ai.dto
 * FileName    : EmbeddingPostRequest
 * Author      : Baekgwa
 * Date        : 25. 11. 12.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 12.     Baekgwa               Initial creation
 */
public record EmbeddingPostRequest(List<Long> postIdList) {
}
