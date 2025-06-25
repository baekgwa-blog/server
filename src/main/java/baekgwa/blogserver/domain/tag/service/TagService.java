package baekgwa.blogserver.domain.tag.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import baekgwa.blogserver.domain.tag.dto.TagRequest;
import baekgwa.blogserver.domain.tag.dto.TagResponse;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.model.tag.entity.TagEntity;
import baekgwa.blogserver.model.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.tag.service
 * FileName    : TagService
 * Author      : Baekgwa
 * Date        : 2025-06-16
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-16     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class TagService {

	private final TagRepository tagRepository;

	@Transactional
	public void createTag(TagRequest.CreateTag request) {
		// 1. 중복 검증
		if (tagRepository.existsByName(request.getName())) {
			throw new GlobalException(ErrorCode.DUPLICATION_TAG);
		}

		// 2. Entity 생성 및 저장
		TagEntity tagEntity = TagEntity.of(request.getName());
		tagRepository.save(tagEntity);
	}

	@Transactional
	public void deleteTag(String tagName) {
		long deleteCount = tagRepository.deleteByName(tagName);

		if (deleteCount <= 0) {
			throw new GlobalException(ErrorCode.NOT_EXIST_TAG);
		}
	}

	@Transactional(readOnly = true)
	public List<TagResponse.TagList> searchTag(String tagName) {

		List<TagEntity> findTagList;
		if (!StringUtils.hasText(tagName)) {
			findTagList = tagRepository.findAll();
		} else {
			findTagList = tagRepository.findByNameContaining(tagName);
		}

		return TagResponse.TagList.from(findTagList);
	}
}
