package baekgwa.blogserver.model.post.post.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.model.post.post.repository
 * FileName    : PostRepositoryTest
 * Author      : Baekgwa
 * Date        : 25. 11. 4.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 4.     Baekgwa               Initial creation
 */
@Transactional
class PostRepositoryTest extends SpringBootTestSupporter {

	@DisplayName("특정 post 들의 조회수(viewCount) 를 증가 시킵니다.")
	@Test
	void bulkUpdateViewCounts1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);

		AtomicLong al = new AtomicLong(1L);
		Map<Long, Long> request = savePostList
			.stream().collect(Collectors.toMap(PostEntity::getId, post -> al.getAndIncrement()));

		// when
		postRepository.bulkUpdateViewCounts(request);

		// then
		List<PostEntity> findPostList = postRepository.findAll();
		assertThat(findPostList).hasSize(2)
			.satisfiesExactlyInAnyOrder(
				post1 -> {
					assertThat(post1.getId()).isEqualTo(savePostList.getFirst().getId());
					assertThat(post1.getViewCount()).isEqualTo(1);
				},
				post2 -> {
					assertThat(post2.getId()).isEqualTo(savePostList.getLast().getId());
					assertThat(post2.getViewCount()).isEqualTo(2);
				}
			);
	}
}