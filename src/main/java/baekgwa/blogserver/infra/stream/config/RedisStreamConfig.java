package baekgwa.blogserver.infra.stream.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;

import baekgwa.blogserver.infra.stream.RedisStreamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.stream.config
 * FileName    : RedisStreamConfig
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : Redis Stream 및 Consumer Group 초기화 설정
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

	private final StringRedisTemplate stringRedisTemplate;

	@Bean
	public ApplicationRunner streamInitializer() {
		return args -> {
			initStreamAndGroup(RedisStreamKeys.POST_EMBEDDING_EVENTS, RedisStreamKeys.EMBEDDING_GROUP);
			initStreamAndGroup(RedisStreamKeys.USER_BEHAVIOR_EVENTS, RedisStreamKeys.BEHAVIOR_GROUP);
		};
	}

	private void initStreamAndGroup(String streamKey, String groupName) {
		try {
			stringRedisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), groupName);
			log.info("Created consumer group '{}' for stream '{}'", groupName, streamKey);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			String msg = cause != null ? cause.getMessage() : e.getMessage();

			if (msg != null && msg.contains("BUSYGROUP")) {
				log.debug("Consumer group '{}' already exists for stream '{}'", groupName, streamKey);
			} else {
				log.error("Failed to initialize consumer group '{}' for stream '{}'", groupName, streamKey, e);
			}
		}
	}
}
