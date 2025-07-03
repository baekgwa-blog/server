package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import baekgwa.blogserver.global.environment.S3Properties;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
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
@RequiredArgsConstructor
public class S3Config {

	private final S3Properties s3Properties;

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.region(Region.of(s3Properties.getRegion()))
			.credentialsProvider(
				StaticCredentialsProvider.create(
					AwsBasicCredentials.create(
						s3Properties.getAccessKey(),
						s3Properties.getSecretKey()
					)
				)
			)
			.build();
	}
}
