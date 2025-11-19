package baekgwa.blogserver.infra.view.scheduler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.infra.view.scheduler
 * FileName    : ViewCountBatchServiceTest
 * Author      : Baekgwa
 * Date        : 25. 11. 17.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 17.     Baekgwa               Initial creation
 */
@Transactional
class ViewCountBatchServiceTest extends SpringBootTestSupporter {

	@DisplayName("게시글을 동기화 합니다. 주기적으로 스케줄 처리됩니다.")
	@Test
	void synchronizeViewCounts1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();
		Long postId = savePost.getId();
		Integer beforeViewCount = savePost.getViewCount();
		Long addViewCount = 1L;

		// stubbing
		Map<Long, Long> mockViewCount = Map.of(postId, addViewCount);
		Mockito.when(viewCountStore.getAllViewCount(any())).thenReturn(mockViewCount);

		// when
		viewCountBatchService.synchronizeViewCounts();

		// then
		PostEntity findPost = postRepository.findAll().getFirst();
		assertThat(findPost.getViewCount()).isEqualTo(1 + beforeViewCount);
	}
}