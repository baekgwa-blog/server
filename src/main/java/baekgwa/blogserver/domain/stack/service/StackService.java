package baekgwa.blogserver.domain.stack.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.stack.dto.StackRequest;
import baekgwa.blogserver.domain.stack.dto.StackResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;
import baekgwa.blogserver.model.stack.post.repository.StackPostRepository;
import baekgwa.blogserver.model.stack.stack.dto.StackStatsDto;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;
import baekgwa.blogserver.model.stack.stack.repository.StackRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.stack.service
 * FileName    : StackService
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class StackService {

	private final StackRepository stackRepository;
	private final StackPostRepository stackPostRepository;
	private final CategoryRepository categoryRepository;
	private final PostRepository postRepository;

	@Transactional
	public void createNewStackSeries(StackRequest.NewStackSeries request) {
		// 1. 입력값 유효성 검증
		if (stackRepository.existsByTitle(request.getTitle())) {
			throw new GlobalException(ErrorCode.ALREADY_EXIST_STACK_SERIES);
		}

		// 1-2. 작성 글의 Sequence(순서) 입력값 검증
		validateCheckStackPostSequence(request.getStackPostList());

		// 1-3. 해당 포스트 들이 이미, 시리즈에 할당되어있는지 검증.
		// 하나의 포스트는 하나의 시리즈에만 들어갈 수 있음.
		List<Long> postIdList = request.getStackPostList().stream().map(StackRequest.StackPost::getPostId).toList();
		if (stackPostRepository.existsByPostIdIn(postIdList)) {
			throw new GlobalException(ErrorCode.ALREADY_REGISTER_POST_STACK_SERIES);
		}

		// 1-3. 포스팅 글 조회 및 유효성 검사
		List<PostEntity> findPostEntity = postRepository.findAllById(postIdList);
		if (findPostEntity.size() != postIdList.size()) {
			throw new GlobalException(ErrorCode.INVALID_POST_LIST);
		}

		// 2. 카테고리 정보 조회
		CategoryEntity findCategory = categoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_CATEGORY));

		// 2. 신규 스택 등록
		StackEntity newStackSeries =
			StackEntity.of(request.getTitle(), request.getDescription(), findCategory, request.getThumbnailImage());
		StackEntity savedStackSeries =
			stackRepository.save(newStackSeries);

		// 3. 포스팅 글, 신규 스택에 등록
		Map<Long, Long> postSequenceMap = request.getStackPostList()
			.stream()
			.collect(Collectors.toMap(StackRequest.StackPost::getPostId, StackRequest.StackPost::getSequence));

		List<StackPostEntity> newStackPost = findPostEntity.stream()
			.map(post -> StackPostEntity.of(savedStackSeries, post, postSequenceMap.get(post.getId())))
			.toList();
		stackPostRepository.saveAll(newStackPost);
	}

	private void validateCheckStackPostSequence(List<StackRequest.StackPost> stackPostList) {
		// 1. Sequence 값들을 추출하여 리스트로 만듦
		List<Long> sequenceList = stackPostList.stream()
			.map(StackRequest.StackPost::getSequence)
			.sorted()
			.toList();

		// 2. 중복된 Sequence 값이 있는지 확인
		long distinctCount = sequenceList.stream().distinct().count();
		if (distinctCount != sequenceList.size()) {
			throw new GlobalException(ErrorCode.INVALID_STACK_POST_SEQUENCE);
		}

		// 3. 1부터 시작하여 연속적인 값인지 확인
		for (int i = 0; i < sequenceList.size(); i++) {
			if (sequenceList.get(i) != i + 1) {
				throw new GlobalException(ErrorCode.INVALID_STACK_POST_SEQUENCE);
			}
		}
	}

	@Transactional(readOnly = true)
	public StackResponse.StackInfo getRelativeStackPostInfo(Long postId) {
		// 1. Post id 유효성 검사
		if (!postRepository.existsById(postId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_POST);
		}

		// 1. 연결된 Stack 조회
		Optional<StackPostEntity> opFindStackPost = stackPostRepository.findByPostId(postId);

		// 2. 해당 글이 스택에 등록되어있지 않으면 빈값 return
		if (opFindStackPost.isEmpty()) {
			return null;
		}

		// 3. 연관된 스택 조회 후, 스택의 모든 포스트 조회
		StackEntity findStack = opFindStackPost.get().getStack();
		List<StackPostEntity> findStackPostList = stackPostRepository.findAllByStack(findStack);

		// 4. dto 변환 및 return
		List<StackResponse.StackPostInfo> stackPostInfoList = findStackPostList
			.stream()
			.map(StackResponse.StackPostInfo::of)
			.sorted(Comparator.comparing(StackResponse.StackPostInfo::getSequence))
			.toList();

		return StackResponse.StackInfo.of(findStack, stackPostInfoList);
	}

	@Transactional(readOnly = true)
	public List<StackResponse.StackDetailInfo> getAllStack() {
		// 1. Stack 과 관련된 Post 의 각각 수량과 가장 마지막에 Updated 된 정보 확인
		List<StackStatsDto> findStackStatsList = stackRepository.findStackStats();

		// 2. dto return
		return findStackStatsList.stream()
			.map(stat -> StackResponse.StackDetailInfo.of(
				stat.getStack(),
				stat.getPostCount(),
				stat.getLastUpdate()
			))
			.toList();
	}
}
