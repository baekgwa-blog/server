package baekgwa.blogserver.global.entrypoint;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.entrypoint
 * FileName    : CustomAuthenticationEntryPoint
 * Author      : Baekgwa
 * Date        : 2025-06-04
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-04     Baekgwa               Initial creation
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		ErrorCode errorCode = ErrorCode.NEED_LOGIN;
		BaseResponse<Void> errorResponse = BaseResponse.fail(errorCode);
		response.setContentType("application/json");
		response.setStatus(errorCode.getStatus().value());
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
