package baekgwa.blogserver.infra.embedding.service;

import static baekgwa.blogserver.infra.embedding.service.EmbeddingPostMetadataKeys.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import baekgwa.blogserver.global.environment.UrlProperties;
import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
import baekgwa.blogserver.model.embedding.entity.EmbeddingJob;
import baekgwa.blogserver.model.embedding.repository.EmbeddingFailureRepository;
import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
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
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingOpenAiService implements EmbeddingService {

	private final EmbeddingModel embeddingModel;
	private final EmbeddingFailureRepository embeddingFailureRepository;
	private final EmbeddingStore<TextSegment> embeddingStore;
	private final UrlProperties urlProperties;

	// TODO : 실패 복구 전략 추가할 것.
	@Override
	public void createEmbeddingPost(PostEntity post, List<TagEntity> tagList) {
		log.debug("Create embedding for postId={}", post.getId());

		try {
			// 1. content 내용, html -> text 변환 by Jsoup
			String content = Jsoup.parse(post.getContent()).text();

			// 2. metadata 생성
			// todo: 현재, Langchain4j 에서, List 타입을 지원하지 않음.
			// 추후, 업데이트 된다면, List 형태를 추가적으로 전달해서, tag 관련 내용도 필터링 될 수 있도록 구성
			String tags = tagList.stream()
				.map(tag -> tag.getName().toLowerCase())
				.collect(Collectors.joining(","));

			// Title 값을 사용해서 마크다운 형식의 링크로 만드는데, [] 가 있으면 재대로 처리되지 않음.
			String originalTitle = post.getTitle();
			String cleanedTitle = originalTitle.replaceAll("[\\[\\]]", ""); // "[" 와 "]"를 모두 제거

			Metadata metadata = Metadata.from(Map.of(
				ID, post.getId(),
				TITLE, cleanedTitle,
				SLUG, post.getSlug(),
				CATEGORY, post.getCategory().getName().toLowerCase(),
				TAGS, tags,
				DESCRIPTION, post.getDescription(),
				CREATED_AT, post.getCreatedAt().toString(),
				SOURCE, generateOriginSource(urlProperties.getFrontend(), post.getSlug())
			));

			// 3. Document 생성 및 청킹
			Document document = Document.from(content, metadata);
			DocumentSplitter splitter = DocumentSplitters.recursive(1500, 300);
			List<TextSegment> textSegmentList = splitter.split(document)
				.stream()
				.map(segment -> TextSegment.from(segment.text(), metadata))
				.toList();

			// 4. 임베딩 데이터 생성 with OpenAi
			Response<List<Embedding>> embeddingResponse = null;
			embeddingResponse = embeddingModel.embedAll(textSegmentList);
			log.debug("Generated {} embeddings", embeddingResponse.content().size());

			// 5. Vector DB(Elasticsearch) 저장
			embeddingStore.addAll(embeddingResponse.content(), textSegmentList);
			log.debug("Successfully embedded and stored postId={}", post.getId());

		} catch (Exception e) {
			log.warn("Embedding failed for postId={}: {}", post.getId(), e.getMessage());
			EmbeddingFailureEntity failure = EmbeddingFailureEntity.of(post.getId(), e.getMessage(),
				EmbeddingJob.CREATE);
			embeddingFailureRepository.save(failure);
			throw e;
		}
	}

	/**
	 * 입력 문장과 관련있는, 문서 조회
	 * @param sentence 문서
	 */
	@Override
	public EmbeddingSearchResult<TextSegment> searchRetrievalPost(String sentence, @NonNull List<String> filter) {
		log.debug("Searching for: '{}', filter: {}", sentence, filter);

		try {
			// 1. 질문 문장 임베딩 화
			Embedding queryEmbedding = embeddingModel.embed(sentence).content();

			// 2. EmbeddingSearchRequest 생성
			EmbeddingSearchRequest.EmbeddingSearchRequestBuilder requestBuilder = EmbeddingSearchRequest
				.builder()
				.queryEmbedding(queryEmbedding)
				.maxResults(10)
				.minScore(0.6);

			// 2-1. Filter 추가
			if (!filter.isEmpty()) {
				log.debug("add keyword filed : {}", filter);
				List<String> lowerCaseFilter = filter.stream().map(String::toLowerCase).toList();
				Filter categoryFilter = MetadataFilterBuilder.metadataKey(CATEGORY).isIn(lowerCaseFilter);
				requestBuilder.filter(categoryFilter);

				// 현재, Tag 관련 버그 있어서, 추후 수정 필요.
				// Filter tagsFilter = MetadataFilterBuilder.metadataKey(TAGS).isIn(filter);
				// Filter combinedFilter = categoryFilter.or(tagsFilter);
				//
				// requestBuilder.filter(combinedFilter);
			}
			EmbeddingSearchRequest searchRequest = requestBuilder.build();

			// 3. Vector DB 에 조회 후 반환
			return embeddingStore.search(searchRequest);
		} catch (Exception e) {
			log.error("Search failed details:", e);
		}

		return null;
	}

	@Override
	public void deleteEmbeddingPost(Long postId) {
		log.debug("Deleting embedding for postId={}", postId);
		try {
			Filter filter = MetadataFilterBuilder.metadataKey(ID).isEqualTo(postId);
			embeddingStore.removeAll(filter);
			log.debug("Successfully deleted embedding for postId={}", postId);
		} catch (Exception e) {
			EmbeddingFailureEntity failure = EmbeddingFailureEntity.of(postId, e.getMessage(), EmbeddingJob.DELETE);
			embeddingFailureRepository.save(failure);
			log.error("Failed to delete embedding for postId={}: {}", postId, e.getMessage());
			throw e;
		}
	}

	@Override
	public void updateEmbeddingPost(PostEntity post, List<TagEntity> tagList) {
		log.debug("Updating embedding for postId={}", post.getId());

		// 1. 메타데이터 기반 삭제 처리
		deleteEmbeddingPost(post.getId());

		// 2. 신규 포스트 등록
		createEmbeddingPost(post, tagList);
		log.debug("Successfully updating embedding for postId={}", post.getId());
	}

	private String generateOriginSource(String feUrl, String slug) {
		return feUrl + "/blog/" + slug;
	}
}
