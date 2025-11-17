package baekgwa.blogserver.domain.ai.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.ai.dto.EmbeddingPostRequest;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingJob;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.domain.ai.service
 * FileName    : AiServiceTest
 * Author      : Baekgwa
 * Date        : 25. 11. 17.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 17.     Baekgwa               Initial creation
 */
@Transactional
class AiServiceTest extends SpringBootTestSupporter {

	@DisplayName("특정 포스팅 임베딩 처리.")
	@Test
	void embeddingPosts1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);

		List<Long> postIdList = savePostList.stream().map(PostEntity::getId).toList();
		EmbeddingPostRequest request = new EmbeddingPostRequest(postIdList);

		// when
		aiService.embeddingPosts(request);

		// then
	}

	@DisplayName("특정 포스팅 임베딩 처리. 글이 없을때도 오류를 발생하지 않고 그냥 통과합니다.")
	@Test
	void embeddingPosts2() {
		// given
		List<Long> postIdList = List.of(1L, 2L);
		EmbeddingPostRequest request = new EmbeddingPostRequest(postIdList);

		// when
		aiService.embeddingPosts(request);

		// then
	}

	@DisplayName("특정 포스팅 임베딩 처리. Embedding 처리 중, 오류가 발생한다면, EmbeddingFailure Table 에 저장합니다.")
	@Test
	void embeddingPosts3() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);

		List<Long> postIdList = savePostList.stream().map(PostEntity::getId).toList();
		EmbeddingPostRequest request = new EmbeddingPostRequest(postIdList);

		// stubbing
		String message = "테스트오류";
		doThrow(new IllegalArgumentException(message))
			.when(embeddingService)
			.createEmbeddingPost(any(), any());

		// when
		aiService.embeddingPosts(request);

		// then
		List<EmbeddingFailureEntity> findFailureList = embeddingFailureRepository.findAll();
		assertThat(findFailureList).hasSize(2)
			.satisfiesExactlyInAnyOrder(
				fail1 -> {
					assertThat(fail1.getPostId()).isEqualTo(savePostList.getFirst().getId());
					assertThat(fail1.getEmbeddingJob()).isEqualTo(EmbeddingJob.CREATE);
					assertThat(fail1.getReason()).isEqualTo(message);
				},
				fail2 -> {
					assertThat(fail2.getPostId()).isEqualTo(savePostList.getLast().getId());
					assertThat(fail2.getEmbeddingJob()).isEqualTo(EmbeddingJob.CREATE);
					assertThat(fail2.getReason()).isEqualTo(message);
				}
			);
	}
}