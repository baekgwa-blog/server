package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import baekgwa.blogserver.global.environment.S3Properties;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : S3Config
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@Configuration
@Profile("!test")
public class S3Config {

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.region(Region.of(S3Properties.REGION))
			.build();
	}
}
