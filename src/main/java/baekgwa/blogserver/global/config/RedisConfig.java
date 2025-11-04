package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import baekgwa.blogserver.global.environment.RedisProperties;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : RedisConfig
 * Author      : Baekgwa
 * Date        : 25. 11. 3.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 3.     Baekgwa               Initial creation
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(redisProperties.getHost());
		configuration.setPort(Integer.parseInt(redisProperties.getPort()));
		configuration.setPassword(redisProperties.getPassword());
		return new LettuceConnectionFactory(configuration);
	}
}
