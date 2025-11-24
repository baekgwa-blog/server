package baekgwa.blogserver.global.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import baekgwa.blogserver.global.environment.OpenAiProperties;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : OpenAiConfig
 * Author      : Baekgwa
 * Date        : 25. 11. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 */
@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class OpenAiConfig {

	private final OpenAiProperties openAiProperties;

	@Bean
	public EmbeddingModel embeddingModel() {
		return OpenAiEmbeddingModel.builder()
			.apiKey(openAiProperties.getApiKey())
			.modelName(openAiProperties.getEmbeddingModelName())
			.user("baekgwa")
			.build();
	}

	@Bean
	public StreamingChatModel streamingChatModel() {
		return OpenAiStreamingChatModel
			.builder()
			.apiKey(openAiProperties.getApiKey())
			.modelName(openAiProperties.getLlmModelName())
			.build();
	}

	@Bean(name = "openAiRestTemplate")
	public RestTemplate openAiRestTemplate(RestTemplateBuilder builder) {
		return builder
			.rootUri("https://api.openai.com/v1")
			.defaultHeader("Authorization", "Bearer " + openAiProperties.getApiKey())
			.connectTimeout(Duration.ofSeconds(3))
			.readTimeout(Duration.ofSeconds(3))
			.build();
	}
}
