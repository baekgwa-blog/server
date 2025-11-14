package baekgwa.blogserver.domain.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.blogserver.domain.ai.dto.AiRequest;
import baekgwa.blogserver.domain.ai.dto.EmbeddingPostRequest;
import baekgwa.blogserver.domain.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.ai.controller
 * FileName    : AiController
 * Author      : Baekgwa
 * Date        : 25. 11. 10.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 10.     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
@Tag(name = "Ai Controller", description = "Ai 관련 테스트 컨트롤러")
public class AiController {

	private final AiService aiService;

	@PostMapping("/stream/search")
	@Operation(summary = "[AI] 관련 블로그 포스트 찾아주기")
	public SseEmitter searchPosts(
		@Valid @RequestBody AiRequest.AiSearchPost request
	) {
		SseEmitter emitter = new SseEmitter(60_000L);
		aiService.searchPosts(request, emitter);
		return emitter;
	}

	@PostMapping("/post/embedding")
	@Operation(summary = "수동 post embedding 후, vector db 에 저장")
	public void embeddingPost(
		@RequestBody EmbeddingPostRequest request
	) {
		aiService.embeddingPosts(request);
	}
}
