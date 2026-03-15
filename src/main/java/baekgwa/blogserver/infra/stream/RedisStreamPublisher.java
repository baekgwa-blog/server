package baekgwa.blogserver.infra.stream;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.stream
 * FileName    : RedisStreamPublisher
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : Redis Stream 이벤트 발행 컴포넌트
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamPublisher {

	private final StringRedisTemplate stringRedisTemplate;

	public void publishEmbeddingEvent(Map<String, String> fields) {
		try {
			stringRedisTemplate.opsForStream().add(RedisStreamKeys.POST_EMBEDDING_EVENTS, fields);
		} catch (Exception e) {
			log.error("Failed to publish embedding event: {}", e.getMessage());
		}
	}

	@Async
	public void publishBehaviorEvent(Map<String, String> fields) {
		try {
			stringRedisTemplate.opsForStream().add(RedisStreamKeys.USER_BEHAVIOR_EVENTS, fields);
		} catch (Exception e) {
			log.error("Failed to publish behavior event: {}", e.getMessage());
		}
	}
}
