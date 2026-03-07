package baekgwa.blogserver.global.ratelimit;

public record RateLimitResult(
	boolean allowed,
	long limit,
	long remaining,
	long resetAt,
	long retryAfter
) {}
