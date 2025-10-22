package baekgwa.blogserver.domain.stack.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.stack.dto.StackRequest;
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
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList);

		// when
		stackService.createNewStackSeries(request);

		// then
		StackEntity saveStack = stackRepository.findAll().getFirst();
		List<StackPostEntity> saveStackPost = stackPostRepository.findAll();

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
		StackEntity saveStack = stackDataFactory.newStackList(1, saveCategory).getFirst();

		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream().map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet())).toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of(saveStack.getTitle(), "description", saveCategory.getId(), stackPostList);

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
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList);

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
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList);

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
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList);

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
		StackEntity saveStack = stackDataFactory.newStackList(1, saveCategory).getFirst();
		stackDataFactory.newStackPost(saveStack, savePostList);

		AtomicLong al = new AtomicLong(0L);
		List<StackRequest.StackPost> stackPostList =
			savePostList.stream()
				.map(post -> StackRequest.StackPost.of(post.getId(), al.incrementAndGet()))
				.toList();
		StackRequest.NewStackSeries request =
			StackRequest.NewStackSeries.of("title", "description", saveCategory.getId(), stackPostList);

		// when // then
		assertThatThrownBy(() -> stackService.createNewStackSeries(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_REGISTER_POST_STACK_SERIES);
	}
}
