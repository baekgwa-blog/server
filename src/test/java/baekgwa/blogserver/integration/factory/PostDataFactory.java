package baekgwa.blogserver.integration.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.post.tag.entity.PostTagEntity;
import baekgwa.blogserver.model.post.tag.repository.PostTagRepository;
import baekgwa.blogserver.model.tag.entity.TagEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.integration.factory
 * FileName    : PostDataFactory
 * Author      : Baekgwa
 * Date        : 2025-06-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-20     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class PostDataFactory {

	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;
	private final EntityManager em;

	@Transactional
	public List<PostEntity> newPostList(final long count, List<TagEntity> saveTagList, CategoryEntity saveCategory) {

		if (count <= 0) {
			throw new IllegalArgumentException("1개 이상 입력되어야 합니다.");
		}

		List<PostEntity> newPostList = new ArrayList<>();
		List<PostTagEntity> newPostTagList = new ArrayList<>();

		for (int index = 1; index <= count; index++) {
			String title = "제목" + index;
			String content = "내용" + index;
			String thumbnail = "썸네일이미지" + index;
			String slug = "slug-" + index;
			String description = "설명" + index;

			PostEntity newPost = PostEntity.of(title, content, description, thumbnail, slug, saveCategory);
			newPostList.add(newPost);

			for (TagEntity saveTag : saveTagList) {
				PostTagEntity newPostTag = PostTagEntity.of(newPost, saveTag);
				newPostTagList.add(newPostTag);
			}
		}

		List<PostEntity> savePost = postRepository.saveAll(newPostList);
		postTagRepository.saveAll(newPostTagList);

		em.flush();
		em.clear();

		return savePost;
	}
}
