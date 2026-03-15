package baekgwa.blogserver.domain.post.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;

/**
 * PackageName : baekgwa.blogserver.domain.post.dto
 * FileName    : PostResponseTest
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : PostResponse 단위 테스트
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
class PostResponseTest {

	@DisplayName("PostEntity 로부터 GetPostResponse 를 생성합니다.")
	@Test
	void getPostResponseFrom_withTagList() {
		// given
		CategoryEntity category = CategoryEntity.of("test-category");
		PostEntity post = PostEntity.of("title", "content", "description", "thumbnail", "slug", category);
		List<String> tagList = List.of("tag1", "tag2");

		// when
		PostResponse.GetPostResponse response = PostResponse.GetPostResponse.from(post, tagList);

		// then
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getDescription()).isEqualTo("description");
		assertThat(response.getThumbnailImage()).isEqualTo("thumbnail");
		assertThat(response.getSlug()).isEqualTo("slug");
		assertThat(response.getViewCount()).isZero();
		assertThat(response.getCategory()).isEqualTo("test-category");
		assertThat(response.getTagList()).containsExactly("tag1", "tag2");
	}

	@DisplayName("tagList 가 null 이면 빈 리스트로 처리합니다.")
	@Test
	void getPostResponseFrom_withNullTagList() {
		// given
		CategoryEntity category = CategoryEntity.of("test-category");
		PostEntity post = PostEntity.of("title", "content", "description", "thumbnail", "slug", category);

		// when
		PostResponse.GetPostResponse response = PostResponse.GetPostResponse.from(post, null);

		// then
		assertThat(response.getTagList()).isEmpty();
	}
}
