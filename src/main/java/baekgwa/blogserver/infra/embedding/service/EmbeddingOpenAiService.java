package baekgwa.blogserver.infra.embedding.service;

import static baekgwa.blogserver.infra.embedding.service.EmbeddingPostMetadataKeys.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	// todo : elk(vector db)에 저장
	// private final ElasticSearchConnector elastconector;

	@Override
	public void embeddingPostToVector(PostEntity post, List<TagEntity> tagList) {
		// 1. content 내용, html -> text 변환 by Jsoup
		String content = Jsoup.parse(post.getContent()).text();
		log.debug("converted content : {}", content);

		// 2. metadata 생성
		String tags = tagList.stream()
			.map(TagEntity::getName)
			.collect(Collectors.joining(","));

		Metadata metadata = Metadata.from(Map.of(
			ID, post.getId(),
			TITLE, post.getTitle(),
			SLUG, post.getSlug(),
			CATEGORY, post.getCategory().getName(),
			TAGS, tags,
			DESCRIPTION, post.getDescription(),
			CREATED_AT, post.getCreatedAt().toString()
		));
		log.debug("meta data {}", metadata);

		// 3. Document 생성 및 청킹
		Document document = Document.from(content, metadata);
		DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
		List<TextSegment> textSegments = splitter.split(document);

		// 4. 임베딩 데이터 생성
		Response<List<Embedding>> embeddingResponse = embeddingModel.embedAll(textSegments);
		log.debug("Generated {} embeddings", embeddingResponse.content().size());
	}
}
