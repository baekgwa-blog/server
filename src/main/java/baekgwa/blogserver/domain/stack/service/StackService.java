package baekgwa.blogserver.domain.stack.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.stack.dto.StackRequest;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;
import baekgwa.blogserver.model.stack.post.repository.StackPostRepository;
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
		if(stackRepository.existsByTitle(request.getTitle())) {
			throw new GlobalException(ErrorCode.ALREADY_EXIST_STACK_SERIES);
		}

		// 2. 카테고리 정보 조회
		CategoryEntity findCategory = categoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_CATEGORY));

		// 2. 신규 스택 등록
		StackEntity newStackSeries = StackEntity.of(request.getTitle(), request.getDescription(), findCategory);
		StackEntity savedStackSeries = stackRepository.save(newStackSeries);

		// 3. 포스팅 글 조회 및 유효성 검사
		List<PostEntity> findPostEntity = postRepository.findAllById(request.getPostId());
		if(findPostEntity.size() != request.getPostId().size()) {
			throw new GlobalException(ErrorCode.NOT_EXIST_POST);
		}

		// 4. 포스팅 글, 신규 스택에 등록
		List<StackPostEntity> newStackPost = findPostEntity.stream()
			.map(data -> StackPostEntity.of(savedStackSeries, data))
			.toList();
		stackPostRepository.saveAll(newStackPost);
	}
}
