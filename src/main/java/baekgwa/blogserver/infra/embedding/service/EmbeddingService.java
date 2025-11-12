package baekgwa.blogserver.infra.embedding.service;

import java.util.List;

import org.springframework.lang.NonNull;

import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;

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

	void createEmbeddingPost(PostEntity post, List<TagEntity> tagList);

	EmbeddingSearchResult<TextSegment> searchRetrievalPost(String sentence, @NonNull List<String> filter);

	void deleteEmbeddingPost(Long postId);

	void updateEmbeddingPost(PostEntity post, List<TagEntity> tagList);
}
