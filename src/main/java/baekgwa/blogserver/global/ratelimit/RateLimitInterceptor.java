package baekgwa.blogserver.global.ratelimit;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

	private final RateLimitService rateLimitService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (request.getDispatcherType() != DispatcherType.REQUEST) {
			return true;
		}

		RateLimitResult result = rateLimitService.checkAndIncrement(request);

		response.setHeader("X-RateLimit-Limit", String.valueOf(result.limit()));
		response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));
		response.setHeader("X-RateLimit-Reset", String.valueOf(result.resetAt()));

		if (!result.allowed()) {
			response.setHeader("Retry-After", String.valueOf(result.retryAfter()));
			throw new GlobalException(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
		}
		return true;
	}
}
