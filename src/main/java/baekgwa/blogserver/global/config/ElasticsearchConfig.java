package baekgwa.blogserver.global.config;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import baekgwa.blogserver.global.environment.ElasticSearchProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : ElasticsearchConfig
 * Author      : Baekgwa
 * Date        : 25. 11. 10.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 10.     Baekgwa               Initial creation
 */
@Profile("!test")
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

	private final ElasticSearchProperties elasticSearchProperties;

	@Bean
	public EmbeddingStore<TextSegment> embeddingStore() throws IOException {
		RestClient restClient = RestClient
			.builder(HttpHost.create(elasticSearchProperties.getUrl()))
			.build();

		ensureIndexExists(restClient, elasticSearchProperties.getEmbeddingIndexName());

		return ElasticsearchEmbeddingStore
			.builder()
			.indexName(elasticSearchProperties.getEmbeddingIndexName())
			// .dimension() // deprecated
			.restClient(restClient)
			.build();
	}

	private void ensureIndexExists(RestClient client, String indexName) throws IOException {
		try {
			Response response = client.performRequest(new Request("HEAD", "/" + indexName));
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.debug("Elasticsearch index [{}] already exists.", indexName);
				return;
			}
		} catch (ResponseException e) {
			if (e.getResponse().getStatusLine().getStatusCode() != 404) {
				throw e;
			}
		}

		log.info("Creating Elasticsearch index [{}] with custom settings...", indexName);
		Request createRequest = new Request("PUT", "/" + indexName);
		createRequest.setJsonEntity("""
			{
			  "settings": {
			    "index.mapping.exclude_source_vectors": false
			  },
			  "mappings": {
			    "properties": {
			      "text": { "type": "text" },
			      "metadata": { "type": "object" },
			      "vector": {
			        "type": "dense_vector",
			        "dims": 3072,
			        "index": true,
			        "similarity": "cosine"
			      }
			    }
			  }
			}
			""");
		client.performRequest(createRequest);
		log.debug("Elasticsearch index [{}] created successfully.", indexName);
	}
}
