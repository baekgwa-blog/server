package baekgwa.blogserver.domain.post.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.post.dto.PostRequest;
import baekgwa.blogserver.domain.post.dto.PostResponse;
import baekgwa.blogserver.domain.post.type.PostListSort;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.PageResponse;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.tag.entity.PostTagEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.domain.post.service
 * FileName    : PostServiceTest
 * Author      : Baekgwa
 * Date        : 2025-06-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-20     Baekgwa               Initial creation
 */
@Transactional
class PostServiceTest extends SpringBootTestSupporter {

	@DisplayName("포스트 글 생성")
	@Test
	void create1() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<Long> saveTagIdList = saveTagList.stream().map(TagEntity::getId).toList();
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		PostRequest.CreatePost request =
			PostRequest.CreatePost.of("제목", "내용", "설명", "썸네일이미지", saveTagIdList, saveCategory.getId());

		// when
		PostResponse.CreatePostResponse response = postService.create(request);

		// then
		PostEntity findPost = postRepository.findAll().getFirst();
		List<PostTagEntity> findPostTagList = postTagRepository.findAll();
		assertThat(response.getSlug()).isEqualTo("제목");
		assertThat(findPost.getContent()).isEqualTo("내용");
		assertThat(findPost.getId()).isNotNull();
		assertThat(findPost.getViewCount()).isZero();
		assertThat(findPost.getCategory().getId()).isEqualTo(saveCategory.getId());
		assertThat(findPost.getThumbnailImage()).isEqualTo("썸네일이미지");
		assertThat(findPostTagList).hasSize(2);
	}

	@DisplayName("제목이 중복되면, 글 작성이 실패합니다.")
	@Test
	void create2() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<Long> saveTagIdList = saveTagList.stream().map(TagEntity::getId).toList();
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();
		PostRequest.CreatePost request =
			PostRequest.CreatePost.of(savePost.getTitle(), "내용", "설명", "썸네일이미지", saveTagIdList, saveCategory.getId());

		// when // then
		assertThatThrownBy(() -> postService.create(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DUPLICATION_POST_TITLE);
	}

	@DisplayName("없는 카테고리로 글작성 하려고 하면, 글 작성이 실패합니다.")
	@Test
	void create3() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<Long> saveTagIdList = saveTagList.stream().map(TagEntity::getId).toList();
		PostRequest.CreatePost request = PostRequest.CreatePost.of("제목", "내용", "설명", "썸네일이미지", saveTagIdList, 1L);

		// when // then
		assertThatThrownBy(() -> postService.create(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_CATEGORY);
	}

	@DisplayName("글의 태그중, 없는 태그가 들어오면, 글작성에 실패합니다.")
	@Test
	void create4() {
		// given
		List<Long> saveTagIdList = new ArrayList<>(
			tagDataFactory.newTagList(2).stream()
				.map(TagEntity::getId)
				.toList()
		);
		saveTagIdList.add(-1L);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		PostRequest.CreatePost request =
			PostRequest.CreatePost.of("제목", "내용", "설명", "썸네일이미지", saveTagIdList, saveCategory.getId());

		// when // then
		assertThatThrownBy(() -> postService.create(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_TAG_LIST);
	}

	@DisplayName("글 작성 시, 썸네일 이미지가 비어있고, 글 내용에 이미지가 있으면, 해당 이미지를 썸네일 이미지로 사용합니다.")
	@Test
	void create5() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<Long> saveTagIdList = saveTagList.stream().map(TagEntity::getId).toList();
		Long saveCategoryId = categoryDataFactory.newCategoryList(1).getFirst().getId();
		PostRequest.CreatePost request =
			PostRequest.CreatePost.of("제목",
				"<img src=\"https://test.com/image.png\">",
				"", "", saveTagIdList, saveCategoryId);

		// when
		PostResponse.CreatePostResponse response = postService.create(request);
		PostEntity findPost = postRepository.findAll().getFirst();

		// then
		assertThat(response.getSlug()).isEqualTo("제목");
		assertThat(findPost.getThumbnailImage()).isEqualTo("https://test.com/image.png");
	}

	@DisplayName("글 작성 시, 썸네일 이미지가 비어있고, 글 내용에도 이미가 없다면, 썸네일 이미지는 없습니다.")
	@Test
	void create6() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<Long> saveTagIdList = saveTagList.stream().map(TagEntity::getId).toList();
		Long saveCategoryId = categoryDataFactory.newCategoryList(1).getFirst().getId();
		PostRequest.CreatePost request = PostRequest.CreatePost.of("제목", "내용", "", "", saveTagIdList, saveCategoryId);

		// when
		postService.create(request);
		PostEntity findPost = postRepository.findAll().getFirst();

		// then
		assertThat(findPost.getThumbnailImage()).isNull();
	}

	@DisplayName("작성되어있는 글을 상세 조회합니다.")
	@Test
	void getPostDetail1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();

		// when
		PostResponse.GetPostDetailResponse response = postService.getPostDetail(savePost.getSlug(), REMOTE_ADDR);

		// then
		assertThat(response)
			.extracting("title", "content", "thumbnailImage", "slug", "category")
			.containsExactly(savePost.getTitle(), savePost.getContent(), savePost.getThumbnailImage(),
				savePost.getSlug(), savePost.getCategory().getName());
		List<String> saveTagNameList = saveTagList.stream().map(TagEntity::getName).toList();
		assertThat(response.getTagList()).hasSize(2)
			.containsExactlyInAnyOrderElementsOf(saveTagNameList);
	}

	@DisplayName("없는 글을 살제 조회하면, 오류 메세지를 서빙합니다.")
	@Test
	void getPostDetail2() {
		// given

		// when // then
		assertThatThrownBy(() -> postService.getPostDetail("없는Slug", REMOTE_ADDR))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_POST);
	}

	@DisplayName("글 목록을 조회합니다. 키워드 검색이 가능합니다.")
	@Test
	void getPostList1() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(10, saveTagList, saveCategory).getFirst();

		// when
		PageResponse<PostResponse.GetPostResponse> response =
			postService.getPostList("목2", 0, 10, null, PostListSort.LATEST);

		// then
		assertThat(response.getPageNo()).isZero();
		assertThat(response.getPageSize()).isEqualTo(10);
		assertThat(response.getTotalElements()).isEqualTo(1);
		assertThat(response.getTotalPages()).isEqualTo(1);
		assertThat(response.isLast()).isTrue();
		assertThat(response.isFirst()).isTrue();
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.isHasPrevious()).isFalse();
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().getFirst().getTitle()).isEqualTo("제목2");
	}

	@DisplayName("글 목록을 조회합니다. 없는 카테고리로 검색하려고 하면, 오류가 발생합니다.")
	@Test
	void getPostList2() {
		// given

		// when // then
		assertThatThrownBy(() -> postService.getPostList(null, 0, 10, "없는 카테고리", PostListSort.LATEST))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_CATEGORY);
	}

	@DisplayName("글 목록을 조회합니다. 페이지는 0 이상, 사이즈는 1 이상이어야 합니다.")
	@Test
	void getPostList3() {
		// given

		// when // then
		assertThatThrownBy(() -> postService.getPostList(null, -1, 0, null, PostListSort.LATEST))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_PAGINATION_PARAMETER);
	}

	@DisplayName("글 목록을 조회합니다. 카테고리로 조회가 가능합니다.")
	@Test
	void getPostList4() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(10, saveTagList, saveCategory).getFirst();

		// when
		PageResponse<PostResponse.GetPostResponse> response =
			postService.getPostList(null, 0, 10, saveCategory.getName(), PostListSort.LATEST);

		// then
		assertThat(response.getPageNo()).isZero();
		assertThat(response.getPageSize()).isEqualTo(10);
		assertThat(response.getTotalElements()).isEqualTo(10);
		assertThat(response.getTotalPages()).isEqualTo(1);
		assertThat(response.isLast()).isTrue();
		assertThat(response.isFirst()).isTrue();
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.isHasPrevious()).isFalse();
		assertThat(response.getContent()).hasSize(10);
	}

	@DisplayName("글 목록을 조회합니다. 조회수 대로 정렬이 가능합니다.")
	@Test
	void getPostList5() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(10, saveTagList, saveCategory);

		// when
		PageResponse<PostResponse.GetPostResponse> response =
			postService.getPostList(null, 0, 10, null, PostListSort.VIEW);

		// then
		assertThat(response.getPageNo()).isZero();
		assertThat(response.getPageSize()).isEqualTo(10);
		assertThat(response.getTotalElements()).isEqualTo(10);
		assertThat(response.getTotalPages()).isEqualTo(1);
		assertThat(response.isLast()).isTrue();
		assertThat(response.isFirst()).isTrue();
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.isHasPrevious()).isFalse();
		assertThat(response.getContent()).hasSize(10);
	}

	@DisplayName("글 목록을 조회합니다. 오래된순으로 정렬이 가능합니다.")
	@Test
	void getPostList6() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		postDataFactory.newPostList(10, saveTagList, saveCategory);

		// when
		PageResponse<PostResponse.GetPostResponse> response =
			postService.getPostList(null, 0, 10, null, PostListSort.OLDEST);

		// then
		assertThat(response.getPageNo()).isZero();
		assertThat(response.getPageSize()).isEqualTo(10);
		assertThat(response.getTotalElements()).isEqualTo(10);
		assertThat(response.getTotalPages()).isEqualTo(1);
		assertThat(response.isLast()).isTrue();
		assertThat(response.isFirst()).isTrue();
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.isHasPrevious()).isFalse();
		assertThat(response.getContent()).hasSize(10);
	}

	@DisplayName("글 목록을 조회합니다. 찾을 데이터가 없다면, 빈 리스트가 반환됩니다.")
	@Test
	void getPostList7() {
		// given

		// when
		PageResponse<PostResponse.GetPostResponse> response =
			postService.getPostList(null, 0, 10, null, PostListSort.OLDEST);

		// then
		assertThat(response.getPageNo()).isZero();
		assertThat(response.getPageSize()).isEqualTo(10);
		assertThat(response.getTotalElements()).isZero();
		assertThat(response.getTotalPages()).isZero();
		assertThat(response.isLast()).isTrue();
		assertThat(response.isFirst()).isTrue();
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.isHasPrevious()).isFalse();
		assertThat(response.getContent()).isEmpty();
	}

	@DisplayName("포스트를 삭제 합니다.")
	@Test
	void deletePost1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		PostEntity savePost = postDataFactory.newPostList(1, saveTagList, saveCategory).getFirst();

		// when
		postService.deletePost(savePost.getId());

		// then
		assertThat(postRepository.findById(savePost.getId())).isEmpty();
	}

	@DisplayName("없는 포스트를 삭제하려고 하면, 오류를 발생합니다.")
	@Test
	void deletePost2() {
		// given

		// when // then
		assertThatThrownBy(() -> postService.deletePost(1L))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_POST);
	}
}