package baekgwa.blogserver.domain.post.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import baekgwa.blogserver.domain.post.dto.PostRequest;
import baekgwa.blogserver.domain.post.dto.PostResponse;
import baekgwa.blogserver.domain.post.type.PostListSort;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.PageResponse;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.post.tag.entity.PostTagEntity;
import baekgwa.blogserver.model.post.tag.repository.PostTagRepository;
import baekgwa.blogserver.model.tag.entity.TagEntity;
import baekgwa.blogserver.model.tag.repository.TagRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.post.service
 * FileName    : PostService
 * Author      : Baekgwa
 * Date        : 2025-06-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-19     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;
	private final TagRepository tagRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public PostResponse.CreatePostResponse create(PostRequest.CreatePost request) {
		// 1. 제목 중복 검증 -> 슬러그로 활용됨
		if (postRepository.existsByTitle(request.getTitle())) {
			throw new GlobalException(ErrorCode.DUPLICATION_POST_TITLE);
		}

		// 2. category 유효성 검증 및 Entity 조회
		CategoryEntity findCategory = categoryRepository.findById(request.getCategoryId()).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_EXIST_CATEGORY));

		// 3. tagList 유효성 검증
		List<TagEntity> tagEntityList = tagRepository.findAllById(request.getTagIdList());
		if (tagEntityList.size() != request.getTagIdList().size()) {
			throw new GlobalException(ErrorCode.NOT_EXIST_TAG_LIST);
		}

		// 4. 썸네일 추출
		if (!StringUtils.hasText(request.getThumbnailImage())) {
			// 4-1. 썸네일 이미지 추출
			String thumbnailImage = extractThumbnailByContent(request.getContent());
			// 4-2. 새로운 request 불변 객체 생성
			request = request.withThumbnailImage(thumbnailImage);
		}

		// 5. 슬러그 생성 (제목으로 슬러그 생성)
		String generatedSlug = generateSlug(request.getTitle());

		// 6. 포스트(카테고리 포함) 생성 / 저장
		PostEntity newPost = PostEntity.of(request.getTitle(), request.getContent(), request.getThumbnailImage(),
			generatedSlug, findCategory);
		postRepository.save(newPost);

		// 7. 포스팅 태그 생성 / 저장
		List<PostTagEntity> newPostTag = tagEntityList.stream().map(tag -> PostTagEntity.of(newPost, tag)).toList();
		postTagRepository.saveAll(newPostTag);

		// 8. 응답 생성. 리다이렉션용 slug 주소
		return PostResponse.CreatePostResponse.from(generatedSlug);
	}

	@Transactional(readOnly = true)
	public PostResponse.GetPostDetailResponse getPostDetail(String slug) {
		// 1. 포스팅 글 조회
		PostEntity postEntity = postRepository.findBySlug(slug).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_EXIST_POST));

		// 2. 태그 이름 목록 조회
		List<String> findTagNameList = postTagRepository.findAllByPost(postEntity)
			.stream()
			.map(tag -> tag.getTag().getName())
			.toList();

		return PostResponse.GetPostDetailResponse.of(postEntity, findTagNameList);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostResponse.GetPostResponse> getPostList(
		@Nullable String keyword, int page, int size, @Nullable String category, PostListSort sort
	) {
		// 1. 페이지네이션 파라미터 유효성 검증
		if (page < 0 || size < 1) {
			throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}

		// 1-1. pageRequest 생성
		Pageable pageable = PageRequest.of(page, size);

		// 2. category 유효성 검증
		if (StringUtils.hasText(category) && !categoryRepository.existsByName(category)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_CATEGORY);
		}

		// 3. Entity 조회
		Page<PostResponse.GetPostResponse> findData =
			postRepository.searchPostList(keyword, category, pageable, sort);

		return PageResponse.of(findData);
	}

	@Transactional
	public void deletePost(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_POST);
		}
		postRepository.deleteById(postId);
	}

	private String extractThumbnailByContent(@NonNull String content) {
		// 마크다운 이미지 파싱: ![alt](url)
		Pattern markdownImgPattern = Pattern.compile("!\\[[^\\]]*\\]\\(([^\\)]+)\\)");
		Matcher markdownMatcher = markdownImgPattern.matcher(content);

		// 이미지가 없을 경우
		if (!markdownMatcher.find()) {
			return null;
		}

		return markdownMatcher.group(1);
	}

	private String generateSlug(String title) {
		return title.replace(' ', '-');
	}
}
