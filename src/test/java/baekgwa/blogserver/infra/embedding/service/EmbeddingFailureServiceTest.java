package baekgwa.blogserver.infra.embedding.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingJob;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.service
 * FileName    : EmbeddingFailureServiceTest
 * Author      : Baekgwa
 * Date        : 25. 11. 17.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 17.     Baekgwa               Initial creation
 */
class EmbeddingFailureServiceTest extends SpringBootTestSupporter {

	/**
	 * 테스트 하는 Service 에서, 개별 신규 트렌잭션 생성으로
	 * class 단위 @Transactional 사용 불가하여
	 * 수동 tearDown 설정
	 */
	@AfterEach
	void cleanup() {
		embeddingFailureRepository.deleteAll();
		postTagRepository.deleteAll();
		postRepository.deleteAll();
		tagRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@DisplayName("임베딩 생성에 실패한 포스트를 처리합니다.")
	@Test
	void processSingleFailure1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();
		String reason = "임베딩 실패";
		EmbeddingFailureEntity saveFailure =
			embeddingFailureDataFactory.newFailureList(savePost, reason, EmbeddingJob.CREATE);

		// when
		embeddingFailureService.processSingleFailure(saveFailure);

		// then
		List<EmbeddingFailureEntity> findList = embeddingFailureRepository.findAll();
		assertThat(findList).isEmpty();
	}

	@DisplayName("임베딩 삭제에 실패한 포스트를 처리합니다.")
	@Test
	void processSingleFailure2() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();
		String reason = "임베딩 삭제 실패";
		EmbeddingFailureEntity saveFailure = embeddingFailureDataFactory.newFailureList(savePost, reason,
			EmbeddingJob.DELETE);

		// when
		embeddingFailureService.processSingleFailure(saveFailure);

		// then
		List<EmbeddingFailureEntity> findList = embeddingFailureRepository.findAll();
		assertThat(findList).isEmpty();
	}
}