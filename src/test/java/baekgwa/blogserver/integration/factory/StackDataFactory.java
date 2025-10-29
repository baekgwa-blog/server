package baekgwa.blogserver.integration.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;
import baekgwa.blogserver.model.stack.post.repository.StackPostRepository;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;
import baekgwa.blogserver.model.stack.stack.repository.StackRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.integration.factory
 * FileName    : StackDataFactory
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class StackDataFactory {

	private final StackRepository stackRepository;
	private final StackPostRepository stackPostRepository;
	private final EntityManager em;

	public List<StackEntity> newStack(final long count, final CategoryEntity category) {

		if (count <= 0) {
			throw new IllegalArgumentException("1개 이상 입력되어야 합니다.");
		}

		List<StackEntity> stackList = new ArrayList<>();
		for (int index = 1; index <= count; index++) {
			StackEntity newStack = StackEntity.of(
				String.format("%s%d", "title", index),
				String.format("%s%d", "description", index),
				category,
				null);

			stackList.add(newStack);
		}
		List<StackEntity> saveStackList = stackRepository.saveAll(stackList);

		em.flush();
		em.clear();

		return saveStackList;
	}

	public List<StackPostEntity> newStackPost(StackEntity stack, List<PostEntity> postList) {

		AtomicLong al = new AtomicLong(0L);
		List<StackPostEntity> stackPostList =
			postList.stream().map(post -> StackPostEntity.of(stack, post, al.incrementAndGet())).toList();

		List<StackPostEntity> saveStackPostList = stackPostRepository.saveAll(stackPostList);

		em.flush();
		em.clear();

		return saveStackPostList;
	}
}
