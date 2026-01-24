package baekgwa.blogserver.global.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.global.cache
 * FileName    : RedisCacheConfig
 * Author      : Baekgwa
 * Date        : 26. 1. 24.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 26. 1. 24.     Baekgwa               Initial creation
 */
@Profile("!test")
@Configuration
@EnableCaching
@Slf4j
public class RedisCacheConfig {

	@Bean
	public CacheManager cacheManager(
		RedisConnectionFactory connectionFactory,
		ObjectMapper rootObjectMapper
	) {
		ObjectMapper redisObjectMapper = rootObjectMapper.copy();

		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
			.allowIfBaseType(Object.class)
			.build();
		redisObjectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

		RedisCacheConfiguration defaultConf = RedisCacheConfiguration.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(Duration.ofMinutes(30)) //default 30분
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

		Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
		for (CacheType cacheType : CacheType.values()) {
			log.debug("Creating cache configuration for cache type {}", cacheType);
			configurations.put(
				cacheType.getCacheName(),
				defaultConf.entryTtl(Duration.ofMinutes(cacheType.getTtlMinutes()))
			);
		}

		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(connectionFactory)
			.cacheDefaults(defaultConf)
			.withInitialCacheConfigurations(configurations)
			.build();
	}
}
