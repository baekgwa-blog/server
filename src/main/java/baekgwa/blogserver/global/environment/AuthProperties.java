package baekgwa.blogserver.global.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * PackageName : baekgwa.blogserver.global.environment
 * FileName    : AuthProperties
 * Author      : Baekgwa
 * Date        : 2025-06-04
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-04     Baekgwa               Initial creation
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
	private String loginId;
	private String loginPw;
	private String secretKey;
	private Long tokenExpiration;
}
