package baekgwa.blogserver.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : SwaggerConfig
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("백과 블로그 API")
				.version("v1")
				.description("백과 블로그 서비스의 OpenAPI 문서입니다.")
				.contact(new Contact()
					.name("Baekgwa")
					.email("baekgwa@example.com")
				)
			)
			.servers(List.of(
				new Server().url("http://localhost:8080").description("로컬 서버"),
				new Server().url("https://blog.api.baekgwa.site").description("배포 서버")
			));
	}
}
