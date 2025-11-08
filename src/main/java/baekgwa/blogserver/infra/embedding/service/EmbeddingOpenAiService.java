package baekgwa.blogserver.infra.embedding.service;

import static baekgwa.blogserver.infra.embedding.service.EmbeddingPostMetadataKeys.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import baekgwa.blogserver.global.environment.UrlProperties;
import baekgwa.blogserver.model.embedding.entity.EmbeddingFailureEntity;
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
	private final UrlProperties urlProperties;

	// TODO : 실패 복구 전략 추가할 것.
	@Override
	public void embeddingPostToVector(PostEntity post, List<TagEntity> tagList) {
		// 1. content 내용, html -> text 변환 by Jsoup
		String content = Jsoup.parse(post.getContent()).text();
		log.debug("converted content : {}", content);

		// 2. metadata 생성
		String tags = tagList.stream()
			.map(TagEntity::getName)
			.collect(Collectors.joining(","));

		log.debug("urlProperties.getFrontend() = {}", urlProperties.getFrontend());

		Metadata metadata = Metadata.from(Map.of(
			ID, post.getId(),
			TITLE, post.getTitle(),
			SLUG, post.getSlug(),
			CATEGORY, post.getCategory().getName(),
			TAGS, tags,
			DESCRIPTION, post.getDescription(),
			CREATED_AT, post.getCreatedAt().toString(),
			SOURCE, generateOriginSource(urlProperties.getFrontend(), post.getSlug())
		));
		log.debug("meta data {}", metadata);

		// 3. Document 생성 및 청킹
		Document document = Document.from(content, metadata);
		DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
		List<TextSegment> textSegmentList = splitter.split(document)
			.stream()
			.map(segment -> TextSegment.from(segment.text(), metadata))
			.toList();

		// 4. 임베딩 데이터 생성 with OpenAi
		Response<List<Embedding>> embeddingResponse = null;
		try {
			embeddingResponse = embeddingModel.embedAll(textSegmentList);
			log.debug("Generated {} embeddings", embeddingResponse.content().size());
		} catch (Exception e) {
			log.warn("Embedding failed for postId={}: {}", post.getId(), e.getMessage());
			EmbeddingFailureEntity failure = EmbeddingFailureEntity.of(post.getId(), e.getMessage());
			embeddingFailureRepository.save(failure);
			return;
		}
	}

	private String generateOriginSource(String feUrl, String slug) {
		return feUrl + "/" + slug;
	}
}
