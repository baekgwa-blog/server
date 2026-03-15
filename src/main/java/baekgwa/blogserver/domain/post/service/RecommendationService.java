package baekgwa.blogserver.domain.post.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.blogserver.domain.post.dto.PostResponse;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.post.tag.entity.PostTagEntity;
import baekgwa.blogserver.model.post.tag.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.domain.post.service
 * FileName    : RecommendationService
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : Redis 기반 개인화/글로벌 추천 서비스
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

	private static final String SESSION_RECOMMENDATION_KEY_PREFIX = "recommendation:session:";
	private static final String GLOBAL_TRENDING_KEY = "recommendation:global:trending";

	private final StringRedisTemplate stringRedisTemplate;
	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;

	@Transactional(readOnly = true)
	public List<PostResponse.GetPostResponse> getRecommendations(String sessionId, int size) {
		// 1. 세션 기반 추천 조회
		List<String> postIdStrings = stringRedisTemplate.opsForList()
			.range(SESSION_RECOMMENDATION_KEY_PREFIX + sessionId, 0, size - 1L);

		// 2. 비어있으면 글로벌 트렌딩 폴백
		if (postIdStrings == null || postIdStrings.isEmpty()) {
			Set<String> trendingIds = stringRedisTemplate.opsForZSet()
				.reverseRange(GLOBAL_TRENDING_KEY, 0, size - 1L);
			if (trendingIds != null) {
				postIdStrings = List.copyOf(trendingIds);
			}
		}

		if (postIdStrings == null || postIdStrings.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> ids = postIdStrings.stream()
			.map(id -> {
				try {
					return Long.parseLong(id);
				} catch (NumberFormatException e) {
					return null;
				}
			})
			.filter(Objects::nonNull)
			.toList();

		if (ids.isEmpty()) {
			return Collections.emptyList();
		}

		// 3. 포스트 조회 (category eager load)
		List<PostEntity> posts = postRepository.findAllByIdIn(ids);

		// 4. 태그 배치 조회
		List<PostTagEntity> allPostTags = postTagRepository.findAllByPostIn(posts);
		Map<Long, List<String>> tagsByPostId = allPostTags.stream()
			.collect(Collectors.groupingBy(
				pt -> pt.getPost().getId(),
				Collectors.mapping(pt -> pt.getTag().getName(), Collectors.toList())
			));

		return posts.stream()
			.map(post -> PostResponse.GetPostResponse.from(post, tagsByPostId.getOrDefault(post.getId(), Collections.emptyList())))
			.toList();
	}
}
