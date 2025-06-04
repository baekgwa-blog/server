package baekgwa.blogserver.domain.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * PackageName : baekgwa.blogserver.domain.health
 * FileName    : HealthController
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "헬스 체크 API")
public class HealthController {

	@Operation(summary = "헬스 체크", description = "서버가 정상적으로 동작하는지 확인합니다.")
	@GetMapping
	public BaseResponse<Void> healthCheck() {
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS);
	}

	@Operation(summary = "로그인 상태 확인", description = "로그인 상태를 확인합니다.")
	@GetMapping("/login")
	public BaseResponse<Void> loginHealthCheck() {
		return BaseResponse.success(SuccessCode.REQUEST_SUCCESS);
	}
}
