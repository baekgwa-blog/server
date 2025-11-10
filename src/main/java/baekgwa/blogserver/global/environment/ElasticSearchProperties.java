package baekgwa.blogserver.global.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * PackageName : baekgwa.blogserver.global.environment
 * FileName    : ElasticSearchProperties
 * Author      : Baekgwa
 * Date        : 25. 11. 10.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 10.     Baekgwa               Initial creation
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchProperties {
	private String url;
	private String embeddingIndexName;
}
