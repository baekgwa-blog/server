package baekgwa.blogserver.global.util;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIpUtils {

	private ClientIpUtils() {}

	/**
	 * 클라이언트 IP를 추출한다.
	 * forward-headers-strategy: framework 설정으로 ForwardedHeaderFilter가 활성화되어 있으므로
	 * getRemoteAddr()은 이미 X-Forwarded-For가 적용된 실제 클라이언트 IP를 반환한다.
	 */
	public static String extract(HttpServletRequest request) {
		return request.getRemoteAddr();
	}
}
