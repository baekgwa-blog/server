package baekgwa.blogserver.infra.embedding.service;

import static baekgwa.blogserver.infra.embedding.service.EmbeddingPostMetadataKeys.*;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.tag.entity.TagEntity;
import dev.langchain4j.data.document.Metadata;
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
		Metadata metadata = Metadata.from(Map.of(
			ID, post.getId(),
			TITLE, post.getTitle(),
			SLUG, post.getSlug(),
			CATEGORY, post.getCategory().getName(),
			TAGS, tagList.stream().map(TagEntity::getName).toList(),
			DESCRIPTION, post.getDescription(),
			CREATED_AT, post.getCreatedAt()
		));
		log.debug("meta data {}", metadata);

		// 3. TextSegment 생성
		TextSegment textSegment = TextSegment.from(content, metadata);

		// 4. 임베딩 데이터 생성
		Response<Embedding> embeddingResponse = embeddingModel.embed(textSegment);
		log.debug("embedding vector size: {}", embeddingResponse.content().vector().length);
	}
}
