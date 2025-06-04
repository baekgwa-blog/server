package baekgwa.blogserver.domain.authentication.service;

import org.springframework.stereotype.Service;

import baekgwa.blogserver.domain.authentication.dto.AuthRequest;
import baekgwa.blogserver.domain.authentication.dto.AuthResponse;
import baekgwa.blogserver.global.environment.AuthProperties;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.util.JWTUtil;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.authentication.service
 * FileName    : AuthService
 * Author      : Baekgwa
 * Date        : 2025-06-04
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-04     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthProperties adminProperties;
	private final JWTUtil jwtUtil;

	public AuthResponse.Login login(AuthRequest.Login requestDto) {
		// 검증
		if(!adminProperties.getLoginId().equals(requestDto.getLoginId()) ||
			!adminProperties.getLoginPw().equals(requestDto.getPassword())) {
			throw new GlobalException(ErrorCode.INVALID_LOGIN_INFO);
		}

		// 새로운 JWT Token 생성
		String accessToken = jwtUtil.createJwt();

		// 토큰 전달
		return AuthResponse.Login.of(accessToken);
	}
}
