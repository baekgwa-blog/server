package baekgwa.blogserver.domain.post.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import baekgwa.blogserver.infra.stream.RedisStreamPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.domain.post.service
 * FileName    : PostEventService
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : 포스트 관련 모든 이벤트 발행 서비스
 *               임베딩 이벤트(포스트 생성/삭제)와 방문자 행동 이벤트를 통합 관리
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation (UserBehaviorService + 임베딩 이벤트 통합)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostEventService {

	private final RedisStreamPublisher redisStreamPublisher;

	// ===== 임베딩 이벤트 =====

	public void publishPostCreated(Long postId, String title, String content, String description,
		String categoryName, List<String> tagNames, String slug) {
		Map<String, String> fields = new HashMap<>();
		fields.put("eventType", "CREATE");
		fields.put("postId", String.valueOf(postId));
		fields.put("title", title);
		fields.put("content", content);
		fields.put("description", description != null ? description : "");
		fields.put("category", categoryName);
		fields.put("tags", String.join(",", tagNames));
		fields.put("slug", slug);
		fields.put("sourceUrl", "");
		fields.put("createdAt", LocalDateTime.now().toString());
		fields.put("occurredAt", LocalDateTime.now().toString());
		redisStreamPublisher.publishEmbeddingEvent(fields);
	}

	public void publishPostDeleted(Long postId) {
		Map<String, String> fields = new HashMap<>();
		fields.put("eventType", "DELETE");
		fields.put("postId", String.valueOf(postId));
		fields.put("occurredAt", LocalDateTime.now().toString());
		redisStreamPublisher.publishEmbeddingEvent(fields);
	}

	// ===== 방문자 행동 이벤트 =====

	public void publishPostViewed(String sessionId, Long postId, String slug) {
		Map<String, String> fields = new HashMap<>();
		fields.put("eventType", "POST_VIEWED");
		fields.put("sessionId", sessionId);
		fields.put("postId", String.valueOf(postId));
		fields.put("slug", slug);
		fields.put("occurredAt", LocalDateTime.now().toString());
		redisStreamPublisher.publishBehaviorEvent(fields);
	}

	public void publishPostSearched(String sessionId, String keyword, String categoryFilter) {
		Map<String, String> fields = new HashMap<>();
		fields.put("eventType", "POST_SEARCHED");
		fields.put("sessionId", sessionId);
		fields.put("keyword", keyword);
		fields.put("categoryFilter", categoryFilter != null ? categoryFilter : "");
		fields.put("occurredAt", LocalDateTime.now().toString());
		redisStreamPublisher.publishBehaviorEvent(fields);
	}
}
