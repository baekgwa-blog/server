package baekgwa.blogserver.global.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * PackageName : baekgwa.blogserver.global.environment
 * FileName    : RedisProperties
 * Author      : Baekgwa
 * Date        : 25. 11. 3.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 3.     Baekgwa               Initial creation
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
	private String host;
	private String port;
	private String password;
}
