package baekgwa.blogserver.domain.ai.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.ai.dto
 * FileName    : AiResponse
 * Author      : Baekgwa
 * Date        : 25. 11. 24.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 24.     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AiResponse {

	@Getter
	@Builder
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class AiHealthCheck {
		private HealthStatus database;
		private HealthStatus llm;
		private boolean isAvailable;
	}

	public enum HealthStatus {
		UP,
		DOWN,
		UNKNOWN
	}
}
