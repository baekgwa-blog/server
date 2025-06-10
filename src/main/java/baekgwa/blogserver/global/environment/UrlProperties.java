package baekgwa.blogserver.global.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * PackageName : baekgwa.blogserver.global.environment
 * FileName    : UrlProperties
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "url")
public class UrlProperties {
	private String frontend;
	private String backend;
}
