package baekgwa.blogserver.infra.embedding.event;

import java.util.List;

import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.event
 * FileName    : EmbeddingUpdatePostEvent
 * Author      : Baekgwa
 * Date        : 25. 11. 11.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 11.     Baekgwa               Initial creation
 */
public record EmbeddingUpdatePostEvent(PostEntity post, List<TagEntity> tagList) {
}
