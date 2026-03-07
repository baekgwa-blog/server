package baekgwa.blogserver.global.ratelimit;

import java.util.List;

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

	private static final DefaultRedisScript<List<Long>> INCREMENT_SCRIPT;

	static {
		INCREMENT_SCRIPT = new DefaultRedisScript<>();
		INCREMENT_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/rate_limit_increment.lua")));
		//noinspection unchecked
		INCREMENT_SCRIPT.setResultType((Class<List<Long>>)(Class<?>) List.class);
	}

	private final StringRedisTemplate stringRedisTemplate;
	private final RateLimitProperties rateLimitProperties;

	public RateLimitResult checkAndIncrement(HttpServletRequest request) {
		String key = "rate:limit:ai:search:" + ClientIpUtils.extract(request);

		List<Long> result = stringRedisTemplate.execute(
			INCREMENT_SCRIPT,
			List.of(key),
			String.valueOf(rateLimitProperties.getWindowSeconds())
		);

		long count = (result != null && result.get(0) != null) ? result.get(0) : 1L;
		long ttl = (result != null && result.get(1) != null && result.get(1) > 0)
			? result.get(1)
			: (long) rateLimitProperties.getWindowSeconds();

		long resetAt = System.currentTimeMillis() / 1000 + ttl;
		long remaining = Math.max(0, rateLimitProperties.getLimit() - count);
		boolean allowed = count <= rateLimitProperties.getLimit();
		long retryAfter = allowed ? 0 : ttl;

		return new RateLimitResult(allowed, rateLimitProperties.getLimit(), remaining, resetAt, retryAfter);
	}
}
