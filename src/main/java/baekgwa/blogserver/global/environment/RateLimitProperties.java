package baekgwa.blogserver.global.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "rate-limit.ai.search")
public class RateLimitProperties {
	private int limit = 10;
	private int windowSeconds = 3600;
}
