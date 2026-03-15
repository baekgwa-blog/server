package baekgwa.blogserver.infra.embedding.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.service
 * FileName    : EmbeddingOpenAiService
 * Author      : Baekgwa
 * Date        : 25. 11. 7.
 * Description : RAG 검색 전용 구현체 (임베딩 생성/삭제는 Data Pipeline 담당)
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 * 2026-03-15     Baekgwa               create/delete 제거, searchRetrievalPost 만 유지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingOpenAiService implements EmbeddingService {

	private static final String CATEGORY = "category";

	private final EmbeddingModel embeddingModel;
	private final EmbeddingStore<TextSegment> embeddingStore;

	@Override
	public EmbeddingSearchResult<TextSegment> searchRetrievalPost(String sentence, @NonNull List<String> filter) {
		log.debug("Searching for: '{}', filter: {}", sentence, filter);

		try {
			// 1. 질문 문장 임베딩화
			Embedding queryEmbedding = embeddingModel.embed(sentence).content();

			// 2. EmbeddingSearchRequest 생성
			EmbeddingSearchRequest.EmbeddingSearchRequestBuilder requestBuilder = EmbeddingSearchRequest
				.builder()
				.queryEmbedding(queryEmbedding)
				.maxResults(10)
				.minScore(0.6);

			// 2-1. Filter 추가
			if (!filter.isEmpty()) {
				log.debug("add keyword filter : {}", filter);
				List<String> lowerCaseFilter = filter.stream().map(String::toLowerCase).toList();
				Filter categoryFilter = MetadataFilterBuilder.metadataKey(CATEGORY).isIn(lowerCaseFilter);
				requestBuilder.filter(categoryFilter);
			}
			EmbeddingSearchRequest searchRequest = requestBuilder.build();

			// 3. Vector DB 에 조회 후 반환
			return embeddingStore.search(searchRequest);
		} catch (Exception e) {
			log.error("Search failed details:", e);
		}

		return null;
	}
}
