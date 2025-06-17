package baekgwa.blogserver.domain.tag.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.tag.dto.TagRequest;
import baekgwa.blogserver.domain.tag.dto.TagResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.integration.SpringBootTestSupporter;
import baekgwa.blogserver.model.tag.entity.TagEntity;

/**
 * PackageName : baekgwa.blogserver.domain.tag.service
 * FileName    : TagServiceTest
 * Author      : Baekgwa
 * Date        : 2025-06-17
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-17     Baekgwa               Initial creation
 */
@Transactional
class TagServiceTest extends SpringBootTestSupporter {

	@DisplayName("새로운 태그를 생성합니다.")
	@Test
	void createTag1() {
		// given
		TagRequest.CreateTag request = TagRequest.CreateTag.of("태그1");

		// when
		tagService.createTag(request);

		// then
		TagEntity findTag = tagRepository.findAll().getFirst();
		assertThat(findTag.getName()).isEqualTo("태그1");
	}

	@DisplayName("중복된 태그는 생성 불가능합니다.")
	@Test
	void createTag2() {
		// given
		String saveTagName = tagDataFactory.newTagList(1).getFirst().getName();
		TagRequest.CreateTag request = TagRequest.CreateTag.of(saveTagName);

		// when // then
		assertThatThrownBy(() -> tagService.createTag(request))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DUPLICATION_TAG);
	}

	@DisplayName("생성된 태그를 삭제합니다.")
	@Test
	void deleteTag1() {
		// given
		TagEntity saveTag = tagDataFactory.newTagList(1).getFirst();

		// when
		tagService.deleteTag(saveTag.getName());

		// then
		Optional<TagEntity> optionalTagEntity = tagRepository.findById(saveTag.getId());
		assertThat(optionalTagEntity).isEmpty();
	}

	@DisplayName("없는 태그를 삭제할 수 없습니다.")
	@Test
	void deleteTag2() {
		// given

		// when // then
		assertThatThrownBy(() -> tagService.deleteTag("없는태그"))
			.isInstanceOf(GlobalException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_EXIST_TAG);
	}

	@DisplayName("태그 목록을 조회합니다. 키워드가 빈값이면 전체 검색으로 진행됩니다.")
	@Test
	void searchTag1() {
		// given
		List<TagEntity> saveTagList = tagDataFactory.newTagList(5);

		// when
		List<TagResponse.TagList> findData = tagService.searchTag("");

		// then
		assertThat(findData).hasSize(5);
		assertThat(findData)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrderElementsOf(TagResponse.TagList.from(saveTagList));
	}

	@DisplayName("태그 목록을 조회합니다. 키워드가 있다면 키워드와 매칭되는것만 조회합니다.")
	@Test
	void searchTag2() {
		// given
		tagDataFactory.newTagList(5);

		// when
		List<TagResponse.TagList> findData = tagService.searchTag("태그1");

		// then
		assertThat(findData).hasSize(1);
		assertThat(findData.getFirst().getName()).isEqualTo("태그1");
	}
}