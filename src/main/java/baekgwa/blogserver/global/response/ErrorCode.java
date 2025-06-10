package baekgwa.blogserver.global.response;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.response
 * FileName    : ErrorCode
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

	//Auth : 1001 ~ 1999
	INVALID_LOGIN_INFO(HttpStatus.BAD_REQUEST, "1001", "잘못된 로그인 정보입니다."),
	EXPIRED_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "1002", "로그인 정보가 만료되었습니다. 다시 로그인 해주세요."),
	NEED_LOGIN(HttpStatus.UNAUTHORIZED, "1003", "로그인이 필요합니다."),

	//Category : 2001 : 2999
	DUPLICATION_CATEGORY(HttpStatus.BAD_REQUEST, "1004", "이미 생성된 카테고리 입니다."),
	NOT_EXIST_CATEGORY(HttpStatus.BAD_REQUEST, "1005", "존재하지 않는 카테고리 입니다."),

	//Common: 9001 ~ 9999
	NOT_FOUND_URL(HttpStatus.NOT_FOUND, "9001", "요청하신 URL 을 찾을 수 없습니다."),
	NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "9002", "요청 메서드를 찾을 수 없습니다."),
	VALIDATION_FAIL_ERROR(HttpStatus.BAD_REQUEST, "9003", ""),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "9004", "올바르지 않은 입력값입니다."),
	HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "9007", "요청이 거부되었습니다."),
	METHOD_ARGUMENT_TYPE_MISS_MATCH(HttpStatus.BAD_REQUEST, "9008", "요청 파라미터 타입 불일치. API 문서 확인해주세요."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9999", "서버 내부 오류 발생했습니다");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
