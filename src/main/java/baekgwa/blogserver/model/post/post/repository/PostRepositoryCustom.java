package baekgwa.blogserver.model.post.post.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import baekgwa.blogserver.domain.post.dto.PostResponse;
import baekgwa.blogserver.domain.post.type.PostListSort;

/**
 * PackageName : baekgwa.blogserver.model.post.post.repository
 * FileName    : PostRepositoryCustom
 * Author      : Baekgwa
 * Date        : 2025-06-24
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-24     Baekgwa               Initial creation
 */
public interface PostRepositoryCustom {

	Page<PostResponse.GetPostResponse> searchPostList(
		@Nullable String keyword,
		@Nullable String category,
		Pageable pageable,
		PostListSort sort
	);

	void bulkUpdateViewCounts(Map<Long, Long> viewCounts);
}
