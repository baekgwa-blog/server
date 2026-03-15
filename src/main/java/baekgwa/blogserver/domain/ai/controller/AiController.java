package baekgwa.blogserver.domain.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import baekgwa.blogserver.domain.ai.dto.AiRequest;
import baekgwa.blogserver.domain.ai.dto.AiResponse;
import baekgwa.blogserver.domain.ai.service.AiService;
import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.SuccessCode;
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
 * 2026-03-15     Baekgwa               수동 임베딩 엔드포인트 제거 (Data Pipeline 이관)
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

	@GetMapping("/health")
	@Operation(summary = "백과 블로그 Chatbot 사용 가능 상태 확인")
	public BaseResponse<AiResponse.AiHealthCheck> chatbotHealthCheck() {
		AiResponse.AiHealthCheck response = aiService.healthCheck();
		return BaseResponse.success(SuccessCode.ENABLE_CHAT_BOT, response);
	}
}
