package baekgwa.blogserver.infra.embedding.service;

import java.util.List;

import baekgwa.blogserver.domain.ai.dto.RetrievalResultDto;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.service
 * FileName    : EmbeddingService
 * Author      : Baekgwa
 * Date        : 25. 11. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 */
public interface EmbeddingService {

	void embeddingPostToVector(PostEntity post, List<TagEntity> tagList);

	List<RetrievalResultDto> searchRetrievalPost(String sentence, Integer topK);
}
