package baekgwa.blogserver.integration.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import baekgwa.blogserver.model.tag.entity.TagEntity;
import baekgwa.blogserver.model.tag.repository.TagRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.integration.factory
 * FileName    : TagDataFactory
 * Author      : Baekgwa
 * Date        : 2025-06-17
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-17     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class TagDataFactory {

	private final TagRepository tagRepository;
	private final EntityManager em;

	public List<TagEntity> newTagList(final long count) {
		if (count <= 0) {
			throw new IllegalArgumentException("1개 이상 입력되어야 합니다.");
		}

		List<TagEntity> newTagList = new ArrayList<>();
		for (int index = 1; index <= count; index++) {
			TagEntity newTag = TagEntity.of(String.format("%s%d", "태그", index));
			newTagList.add(newTag);
		}

		List<TagEntity> saveTagList = tagRepository.saveAll(newTagList);

		em.flush();
		em.clear();

		return saveTagList;
	}
}
