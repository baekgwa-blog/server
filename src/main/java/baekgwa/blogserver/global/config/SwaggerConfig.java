package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

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
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("백과 블로그 API")
				.version("v1")
				.description("백과 블로그 서비스의 Swagger(OpenAPI) 문서입니다.")
				.contact(new Contact()
					.name("Baekgwa")
					.email("baekgwa@example.com")
				)
			);
	}
}
