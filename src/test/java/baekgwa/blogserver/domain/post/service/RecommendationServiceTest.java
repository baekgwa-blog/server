package baekgwa.blogserver.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import baekgwa.blogserver.domain.post.dto.PostResponse;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.post.tag.repository.PostTagRepository;

/**
 * PackageName : baekgwa.blogserver.domain.post.service
 * FileName    : RecommendationServiceTest
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : RecommendationService 단위 테스트
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

	@InjectMocks
	private RecommendationService recommendationService;

	@Mock
	private StringRedisTemplate stringRedisTemplate;

	@Mock
	private PostRepository postRepository;

	@Mock
	private PostTagRepository postTagRepository;

	@Mock
	private ListOperations<String, String> listOperations;

	@Mock
	private ZSetOperations<String, String> zSetOperations;

	@DisplayName("세션 기반 추천 결과가 있으면 해당 결과를 반환합니다.")
	@Test
	void getRecommendations_sessionBased() {
		// given
		String sessionId = "test-session-id";
		List<String> postIdStrings = List.of("1", "2");

		given(stringRedisTemplate.opsForList()).willReturn(listOperations);
		given(listOperations.range("recommendation:session:" + sessionId, 0, 4)).willReturn(postIdStrings);
		given(postRepository.findAllByIdIn(List.of(1L, 2L))).willReturn(Collections.emptyList());
		given(postTagRepository.findAllByPostIn(anyList())).willReturn(Collections.emptyList());

		// when
		List<PostResponse.GetPostResponse> result = recommendationService.getRecommendations(sessionId, 5);

		// then
		assertThat(result).isEmpty();
		verify(postRepository).findAllByIdIn(List.of(1L, 2L));
	}

	@DisplayName("세션 기반 추천이 없으면 글로벌 트렌딩으로 폴백합니다.")
	@Test
	void getRecommendations_globalFallback() {
		// given
		String sessionId = "test-session-id";
		Set<String> trendingIds = new LinkedHashSet<>(List.of("3", "4"));

		given(stringRedisTemplate.opsForList()).willReturn(listOperations);
		given(listOperations.range("recommendation:session:" + sessionId, 0, 4)).willReturn(Collections.emptyList());
		given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRange("recommendation:global:trending", 0, 4)).willReturn(trendingIds);
		given(postRepository.findAllByIdIn(List.of(3L, 4L))).willReturn(Collections.emptyList());
		given(postTagRepository.findAllByPostIn(anyList())).willReturn(Collections.emptyList());

		// when
		List<PostResponse.GetPostResponse> result = recommendationService.getRecommendations(sessionId, 5);

		// then
		assertThat(result).isEmpty();
		verify(postRepository).findAllByIdIn(List.of(3L, 4L));
	}

	@DisplayName("세션과 글로벌 트렌딩 모두 없으면 빈 리스트를 반환합니다.")
	@Test
	void getRecommendations_empty() {
		// given
		String sessionId = "test-session-id";

		given(stringRedisTemplate.opsForList()).willReturn(listOperations);
		given(listOperations.range("recommendation:session:" + sessionId, 0, 4)).willReturn(Collections.emptyList());
		given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRange("recommendation:global:trending", 0, 4)).willReturn(Collections.emptySet());

		// when
		List<PostResponse.GetPostResponse> result = recommendationService.getRecommendations(sessionId, 5);

		// then
		assertThat(result).isEmpty();
		verify(postRepository, never()).findAllByIdIn(anyList());
	}

	@DisplayName("Redis에서 null이 반환되면 빈 리스트를 반환합니다.")
	@Test
	void getRecommendations_nullFromRedis() {
		// given
		String sessionId = "test-session-id";

		given(stringRedisTemplate.opsForList()).willReturn(listOperations);
		given(listOperations.range("recommendation:session:" + sessionId, 0, 4)).willReturn(null);
		given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRange("recommendation:global:trending", 0, 4)).willReturn(null);

		// when
		List<PostResponse.GetPostResponse> result = recommendationService.getRecommendations(sessionId, 5);

		// then
		assertThat(result).isEmpty();
	}
}
