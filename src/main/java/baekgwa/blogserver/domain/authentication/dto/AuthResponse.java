package baekgwa.blogserver.domain.authentication.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.authentication.dto
 * FileName    : AuthResponse
 * Author      : Baekgwa
 * Date        : 2025-06-04
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-04     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Login {
		private String accessToken;

		public static Login of(String accessToken) {
			return new Login(accessToken);
		}
	}
}
