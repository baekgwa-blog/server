package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
