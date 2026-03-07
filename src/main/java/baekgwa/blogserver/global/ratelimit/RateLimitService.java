package baekgwa.blogserver.global.ratelimit;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import baekgwa.blogserver.global.environment.RateLimitProperties;
import baekgwa.blogserver.global.util.ClientIpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {

	private static final DefaultRedisScript<Long> INCREMENT_SCRIPT;

	static {
		INCREMENT_SCRIPT = new DefaultRedisScript<>();
		INCREMENT_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/rate_limit_increment.lua")));
		INCREMENT_SCRIPT.setResultType(Long.class);
	}

	private final StringRedisTemplate stringRedisTemplate;
	private final RateLimitProperties rateLimitProperties;

	public RateLimitResult checkAndIncrement(HttpServletRequest request) {
		String key = "rate:limit:ai:search:" + ClientIpUtils.extract(request);

		Long count = stringRedisTemplate.execute(
			INCREMENT_SCRIPT,
			List.of(key),
			String.valueOf(rateLimitProperties.getWindowSeconds())
		);
		if (count == null) {
			count = 1L;
		}

		Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
		if (ttl == null || ttl < 0) {
			ttl = (long) rateLimitProperties.getWindowSeconds();
		}

		long resetAt = System.currentTimeMillis() / 1000 + ttl;
		long remaining = Math.max(0, rateLimitProperties.getLimit() - count);
		boolean allowed = count <= rateLimitProperties.getLimit();
		long retryAfter = allowed ? 0 : ttl;

		return new RateLimitResult(allowed, rateLimitProperties.getLimit(), remaining, resetAt, retryAfter);
	}
}
