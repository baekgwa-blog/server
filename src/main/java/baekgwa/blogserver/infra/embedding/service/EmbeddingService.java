package baekgwa.blogserver.infra.embedding.service;

import java.util.List;

import org.springframework.lang.NonNull;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.service
 * FileName    : EmbeddingService
 * Author      : Baekgwa
 * Date        : 25. 11. 7.
 * Description : RAG 검색 전용 임베딩 서비스 인터페이스 (임베딩 생성/삭제는 Data Pipeline 담당)
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 * 2026-03-15     Baekgwa               create/delete 임베딩 책임 Data Pipeline 으로 이관 - searchRetrievalPost 만 유지
 */
public interface EmbeddingService {

	EmbeddingSearchResult<TextSegment> searchRetrievalPost(String sentence, @NonNull List<String> filter);
}
