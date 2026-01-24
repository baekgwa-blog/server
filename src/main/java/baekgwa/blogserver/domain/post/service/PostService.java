package baekgwa.blogserver.domain.post.service;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
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
import baekgwa.blogserver.global.cache.CacheType;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.PageResponse;
import baekgwa.blogserver.global.util.SlugUtil;
import baekgwa.blogserver.infra.embedding.event.EmbeddingCreatePostEvent;
import baekgwa.blogserver.infra.embedding.event.EmbeddingDeletePostEvent;
import baekgwa.blogserver.infra.view.event.PostViewEvent;
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
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;
	private final TagRepository tagRepository;
	private final CategoryRepository categoryRepository;

	private final ApplicationEventPublisher eventPublisher;

	@CacheEvict(cacheNames = CacheType.CacheNames.POST_LIST, allEntries = true)
	@Transactional
	public PostResponse.CreatePostResponse create(PostRequest.CreatePost request) {
		if (postRepository.existsByTitle(request.getTitle())) {
			throw new GlobalException(ErrorCode.DUPLICATION_POST_TITLE);
		}

		CategoryEntity findCategory = categoryRepository.findById(request.getCategoryId()).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_EXIST_CATEGORY));

		List<TagEntity> findTagEntityList = tagRepository.findAllById(request.getTagIdList());
		if (findTagEntityList.size() != request.getTagIdList().size()) {
			throw new GlobalException(ErrorCode.NOT_EXIST_TAG_LIST);
		}

		if (!StringUtils.hasText(request.getThumbnailImage())) {
			String thumbnailImage = extractThumbnailByContent(request.getContent());
			request = request.withThumbnailImage(thumbnailImage);
		}

		String generatedSlug = SlugUtil.generateSlug(request.getTitle());

		PostEntity newPost = PostEntity.of(request.getTitle(), request.getContent(), request.getDescription(),
			request.getThumbnailImage(), generatedSlug, findCategory);
		postRepository.save(newPost);

		List<PostTagEntity> newPostTag = findTagEntityList.stream().map(tag -> PostTagEntity.of(newPost, tag)).toList();
		postTagRepository.saveAll(newPostTag);

		eventPublisher.publishEvent(new EmbeddingCreatePostEvent(newPost, findTagEntityList));

		return PostResponse.CreatePostResponse.from(generatedSlug);
	}

	@Transactional(readOnly = true)
	public PostResponse.GetPostDetailResponse getPostDetail(String slug, String remoteAddr) {
		// 1. 포스팅 글 조회
		PostEntity postEntity = postRepository.findBySlug(slug).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_EXIST_POST));

		// 2. 태그 이름 목록 조회
		List<String> findTagNameList = postTagRepository.findAllByPost(postEntity)
			.stream()
			.map(tag -> tag.getTag().getName())
			.toList();

		// 3. viewCount 증가
		eventPublisher.publishEvent(new PostViewEvent(postEntity.getId(), remoteAddr));

		return PostResponse.GetPostDetailResponse.of(postEntity, findTagNameList);
	}

	@Cacheable(
		cacheNames = CacheType.CacheNames.POST_LIST,
		key = "@cacheKeyFactory.getPostListKey(#category, #page, #size, #sort)",
		condition = "#keyword == null || #keyword.isEmpty()",
		unless = "#result == null"
	)
	@Transactional(readOnly = true)
	public PageResponse<PostResponse.GetPostResponse> getPostList(
		@Nullable String keyword,
		int page,
		int size,
		@Nullable String category,
		PostListSort sort
	) {
		log.debug("[Cache Miss] Get Post List, keyword:{}, page:{}, size:{}, category:{}, sort:{}",
			keyword, page, size, category, sort
		);

		if (page < 0 || size < 1) {
			throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}

		Pageable pageable = PageRequest.of(page, size);
		if (StringUtils.hasText(category) && !categoryRepository.existsByName(category)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_CATEGORY);
		}

		Page<PostResponse.GetPostResponse> findData =
			postRepository.searchPostList(keyword, category, pageable, sort);

		return PageResponse.of(findData);
	}

	@CacheEvict(cacheNames = CacheType.CacheNames.POST_LIST, allEntries = true)
	@Transactional
	public void deletePost(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_POST);
		}
		postRepository.deleteById(postId);

		// delete post embedding event 발행
		eventPublisher.publishEvent(new EmbeddingDeletePostEvent(postId));
	}

	private String extractThumbnailByContent(@NonNull String content) {
		Document doc = Jsoup.parse(content);
		Element imgElement = doc.selectFirst("img");

		if (imgElement == null) {
			return null;
		}

		return imgElement.attr("src");
	}
}
