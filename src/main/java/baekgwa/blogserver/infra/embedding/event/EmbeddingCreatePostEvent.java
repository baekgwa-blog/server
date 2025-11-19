package baekgwa.blogserver.infra.embedding.event;

import java.util.List;

import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.event
 * FileName    : EmbeddingCreatePostEvent
 * Author      : Baekgwa
 * Date        : 25. 11. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 */
public record EmbeddingCreatePostEvent(PostEntity post, List<TagEntity> tagList) {
}
