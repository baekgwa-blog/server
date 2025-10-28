package baekgwa.blogserver.domain.stack.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.stack.dto.StackRequest;
import baekgwa.blogserver.domain.stack.dto.StackResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.domain.stack.service
 * FileName    : StackServiceTest
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@Transactional
class StackServiceTest extends SpringBootTestSupporter {

	@DisplayName("신규 스택(시리즈) 생성")
	@Test
	void createNewStackSeries1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet())).toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList, "imageUrl");

		// when
		StackResponse.CreateNewStack newStackSeries = stackService.createNewStackSeries(request);

		// then
		StackEntity saveStack = stackRepository.findAll().getFirst();
		List<StackPostEntity> saveStackPost = stackPostRepository.findAll();

		assertThat(newStackSeries.getStackId()).isEqualTo(saveStack.getId());
		assertThat(saveStack.getTitle()).isEqualTo("title");
		assertThat(saveStack.getDescription()).isEqualTo("description");
		assertThat(saveStack.getCategory().getId()).isEqualTo(saveCategory.getId());
		assertThat(saveStackPost).hasSize(2);
		assertThat(saveStackPost.getFirst().getSequence()).isEqualTo(1L);
		assertThat(saveStackPost.getLast().getSequence()).isEqualTo(2L);
	}

	@DisplayName("신규 스택(시리즈) 생성 / 이미 존재하는 stack 이름이면, 오류를 생성합니다.")
	@Test
	void createNewStackSeries2() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();

		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet())).toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of(saveStack.getTitle(), "description", saveCategory.getId(), stackPostList,
				"imageUrl");

		// when // then
		assertThatThrownBy(() -> stackService.createNewStackSeries(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_EXIST_STACK_SERIES);
	}

	@DisplayName("신규 스택(시리즈) 생성 시, 포스트간 순서(sequence) 는 고유해야 합니다.")
	@Test
	void createNewStackSeries3() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), 1L)).toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList, "imageUrl");

		// when // then
		assertThatThrownBy(() -> stackService.createNewStackSeries(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_STACK_POST_SEQUENCE);
	}

	@DisplayName("신규 스택(시리즈) 생성 시, 포스트간 순서(sequence) 는 연속적이어야 합니다.")
	@Test
	void createNewStackSeries4() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		AtomicLong al = new AtomicLong(1L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet())).toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList, "imageUrl");

		// when // then
		assertThatThrownBy(() -> stackService.createNewStackSeries(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_STACK_POST_SEQUENCE);
	}

	@DisplayName("신규 스택(시리즈) 생성 시, 실제로 존재하는 포스팅 글만 가능합니다.")
	@Test
	void createNewStackSeries5() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream()
				.map(post -> StackRequest.StackPost.of(post.getId() + 1L, al.incrementAndGet()))
				.toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList, "imageUrl");

		// when // then
		assertThatThrownBy(() -> stackService.createNewStackSeries(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_POST_LIST);
	}

	@DisplayName("이미 등록된 다른 스택에 등록된 포스트는 다른 스택에 등록할 수 없습니다.")
	@Test
	void createNewStackSeries6() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream()
				.map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet()))
				.toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList, "imageUrl");

		// when // then
		assertThatThrownBy(() -> stackService.createNewStackSeries(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_REGISTER_POST_STACK_SERIES);
	}

	@DisplayName("현재 입력받은 postId 가 포함된 스택(시리즈)의 목록을 조회합니다.")
	@Test
	void getRelativeStackPostInfo1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		// when
		StackResponse.StackInfo response = stackService.getRelativeStackPostInfo(savePostList.getFirst().getId());

		// then
		assertThat(response.getStackId()).isEqualTo(saveStack.getId());
		assertThat(response.getTitle()).isEqualTo(saveStack.getTitle());
		assertThat(response.getStackPostInfoList()).hasSize(2);

		assertThat(response.getStackPostInfoList()).satisfiesExactly(
			postInfo1 -> {
				assertThat(postInfo1.getPostId()).isEqualTo(savePostList.getFirst().getId());
				assertThat(postInfo1.getTitle()).isEqualTo(savePostList.getFirst().getTitle());
				assertThat(postInfo1.getSlug()).isEqualTo(savePostList.getFirst().getSlug());
				assertThat(postInfo1.getSequence()).isEqualTo(1L);
			},
			postInfo2 -> {
				assertThat(postInfo2.getPostId()).isEqualTo(savePostList.get(1).getId());
				assertThat(postInfo2.getTitle()).isEqualTo(savePostList.get(1).getTitle());
				assertThat(postInfo2.getSlug()).isEqualTo(savePostList.get(1).getSlug());
				assertThat(postInfo2.getSequence()).isEqualTo(2L);
			}
		);
	}

	@DisplayName("현재 입력받은 postId 가 포함된 스택(시리즈)의 목록을 조회합니다. 없다면 빈 응답입니다.")
	@Test
	void getRelativeStackPostInfo2() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(1);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);

		// when
		StackResponse.StackInfo response = stackService.getRelativeStackPostInfo(savePostList.getFirst().getId());

		// then
		assertThat(response).isNull();
	}

	@DisplayName("현재 입력받은 postId 가 포함된 스택(시리즈)의 목록을 조회합니다. 잘못된 글 id 라면 오류를 발생합니다.")
	@Test
	void getRelativeStackPostInfo3() {
		// given

		// when // then
		assertThatThrownBy(() -> stackService.getRelativeStackPostInfo(1L))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_POST);
	}

	@DisplayName("현재 등록된 모든 스택(시리즈)의 정보를 return 합니다.")
	@Test
	void getAllStack1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(4, saveTagList, saveCategory);
		List<StackEntity> saveStackList = stackDataFactory.newStack(2, saveCategory);
		stackDataFactory.newStackPost(saveStackList.getFirst(), List.of(savePostList.getFirst(), savePostList.get(1)));
		stackDataFactory.newStackPost(saveStackList.getLast(), List.of(savePostList.get(2), savePostList.getLast()));

		// when
		List<StackResponse.StackDetailInfo> response = stackService.getAllStack();

		// then
		assertThat(response).hasSize(2).satisfiesExactly(
			stack1 -> {
				assertThat(stack1.getStackId()).isEqualTo(saveStackList.getFirst().getId());
				assertThat(stack1.getTitle()).isEqualTo(saveStackList.getFirst().getTitle());
				assertThat(stack1.getDescription()).isEqualTo(saveStackList.getFirst().getDescription());
				assertThat(stack1.getCategory()).isEqualTo(saveStackList.getFirst().getCategory().getName());
				assertThat(stack1.getThumbnailImage()).isEqualTo(saveStackList.getFirst().getThumbnailImage());
				assertThat(stack1.getCount()).isEqualTo(2L);
			},
			stack2 -> {
				assertThat(stack2.getStackId()).isEqualTo(saveStackList.getLast().getId());
				assertThat(stack2.getTitle()).isEqualTo(saveStackList.getLast().getTitle());
				assertThat(stack2.getDescription()).isEqualTo(saveStackList.getLast().getDescription());
				assertThat(stack2.getCategory()).isEqualTo(saveStackList.getLast().getCategory().getName());
				assertThat(stack2.getThumbnailImage()).isEqualTo(saveStackList.getLast().getThumbnailImage());
				assertThat(stack2.getCount()).isEqualTo(2L);
			}
		);
	}

	@DisplayName("현재 등록된 모든 스택(시리즈)의 정보를 return 합니다. 없으면 빈 배열이 반환됩니다.")
	@Test
	void getAllStack2() {
		// given

		// when
		List<StackResponse.StackDetailInfo> response = stackService.getAllStack();

		// then
		assertThat(response).isEmpty();
	}

	@DisplayName("특정 스택의 정보와 할당된 포스트들을 조회합니다.")
	@Test
	void getStackDetail1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		// when
		StackResponse.StackDetail stackDetail = stackService.getStackDetail(saveStack.getId());

		// then
		assertThat(stackDetail.getStackId()).isEqualTo(saveStack.getId());
		assertThat(stackDetail.getTitle()).isEqualTo(saveStack.getTitle());
		assertThat(stackDetail.getDescription()).isEqualTo(saveStack.getDescription());
		assertThat(stackDetail.getCategory()).isEqualTo(saveStack.getCategory().getName());
		assertThat(stackDetail.getThumbnailImage()).isEqualTo(saveStack.getThumbnailImage());
		assertThat(stackDetail.getStackPostInfoList()).hasSize(2).satisfiesExactly(
			post1 -> {
				assertThat(post1.getPostId()).isEqualTo(savePostList.getFirst().getId());
				assertThat(post1.getTitle()).isEqualTo(savePostList.getFirst().getTitle());
				assertThat(post1.getDescription()).isEqualTo(savePostList.getFirst().getDescription());
				assertThat(post1.getSlug()).isEqualTo(savePostList.getFirst().getSlug());
				assertThat(post1.getThumbnailImage()).isEqualTo(savePostList.getFirst().getThumbnailImage());
			},
			post2 -> {
				assertThat(post2.getPostId()).isEqualTo(savePostList.get(1).getId());
				assertThat(post2.getTitle()).isEqualTo(savePostList.get(1).getTitle());
				assertThat(post2.getDescription()).isEqualTo(savePostList.get(1).getDescription());
				assertThat(post2.getSlug()).isEqualTo(savePostList.get(1).getSlug());
				assertThat(post2.getThumbnailImage()).isEqualTo(savePostList.get(1).getThumbnailImage());
			}
		);
	}

	@DisplayName("특정 스택의 정보와 할당된 포스트들을 조회합니다. 없는 스택을 조회하면 예외를 바생시킵니다.")
	@Test
	void getStackDetail2() {
		// given

		// when // then
		assertThatThrownBy(() -> stackService.getStackDetail(1L))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOTFOUND_STACK);
	}

	@DisplayName("특정 스택의 내용을 수정합니다. PUT 으로 동작합니다.")
	@Test
	void modifyStackSeries1() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet())).toList();
		StackRequest.ModifyStackSeries request = StackRequest.ModifyStackSeries.of("title", "description",
			stackPostList, "thumbnailImage");

		// when
		StackResponse.ModifyStack modifyStack = stackService.modifyStackSeries(saveStack.getId(), request);

		// then
		assertThat(modifyStack.getStackId()).isEqualTo(saveStack.getId());
		StackEntity findStack = stackRepository.findAll().getFirst();
		List<StackPostEntity> findStackPostList = stackPostRepository.findAll();

		assertThat(findStack.getTitle()).isEqualTo(request.getTitle());
		assertThat(findStack.getDescription()).isEqualTo(request.getDescription());
		assertThat(findStack.getThumbnailImage()).isEqualTo(request.getThumbnailImage());
		assertThat(findStackPostList).hasSize(2)
			.satisfiesExactlyInAnyOrder(
				post1 -> {
					assertThat(post1.getPost().getId()).isEqualTo(request.getStackPostList().getFirst().getPostId());
					assertThat(post1.getSequence()).isEqualTo(request.getStackPostList().getFirst().getSequence());
				},
				post2 -> {
					assertThat(post2.getPost().getId()).isEqualTo(request.getStackPostList().getLast().getPostId());
					assertThat(post2.getSequence()).isEqualTo(request.getStackPostList().getLast().getSequence());
				}
			);
	}

	@DisplayName("특정 스택의 내용을 수정합니다. 없는 스택을 요청하면 잘못된 오류루를 발생합니다.")
	@Test
	void modifyStackSeries2() {
		// given
		List<StackRequest.StackPost> stackPostList = List.of(StackRequest.StackPost.of(1L, 1L));
		StackRequest.ModifyStackSeries request = StackRequest.ModifyStackSeries.of("title", "description",
			stackPostList, "thumbnailImage");

		// when // then
		assertThatThrownBy(() -> stackService.modifyStackSeries(0L, request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOTFOUND_STACK);
	}

	@DisplayName("특정 스택의 내용을 수정합니다. 이미 등록된 포스트는 추가 등록이 불가능합니다.")
	@Test
	void modifyStackSeries3() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(4, saveTagList, saveCategory);
		List<StackEntity> saveStackList = stackDataFactory.newStack(2, saveCategory);
		stackDataFactory.newStackPost(saveStackList.getFirst(), List.of(savePostList.get(0), savePostList.get(1)));
		stackDataFactory.newStackPost(saveStackList.getLast(), List.of(savePostList.get(2), savePostList.get(3)));

		AtomicLong al = new AtomicLong(0L);
		List<PostEntity> requestPostList = List.of(savePostList.get(2), savePostList.get(3));
		List<StackRequest.StackPost> stackPostList =
			requestPostList.stream()
				.map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet()))
				.toList();
		StackRequest.ModifyStackSeries request = StackRequest.ModifyStackSeries.of("title", "description",
			stackPostList, "thumbnailImage");

		// when // then
		assertThatThrownBy(() -> stackService.modifyStackSeries(saveStackList.getFirst().getId(), request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_REGISTER_POST_STACK_SERIES);
	}

	@DisplayName("특정 스택의 내용을 수정합니다. 존재하지 않는 포스팅을 할당하려고 하면 오류를 발생합니다.")
	@Test
	void modifyStackSeries4() {
		// given
		CategoryEntity saveCategory = categoryDataFactory.newCategoryList(1).getFirst();
		List<TagEntity> saveTagList = tagDataFactory.newTagList(2);
		List<PostEntity> savePostList = postDataFactory.newPostList(2, saveTagList, saveCategory);
		StackEntity saveStack = stackDataFactory.newStack(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(al.incrementAndGet(), al.get())).toList();
		StackRequest.ModifyStackSeries request = StackRequest.ModifyStackSeries.of("title", "description",
			stackPostList, "thumbnailImage");

		// when // then
		assertThatThrownBy(() -> stackService.modifyStackSeries(saveStack.getId(), request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_POST_LIST);
	}
}
