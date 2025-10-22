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

	//Auth : 1000 ~ 1999
	INVALID_LOGIN_INFO(HttpStatus.BAD_REQUEST, "1000", "잘못된 로그인 정보입니다."),
	EXPIRED_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "1001", "로그인 정보가 만료되었습니다. 다시 로그인 해주세요."),
	NEED_LOGIN(HttpStatus.UNAUTHORIZED, "1002", "로그인이 필요합니다."),

	//Category : 2000 : 2999
	DUPLICATION_CATEGORY(HttpStatus.BAD_REQUEST, "2000", "이미 생성된 카테고리 입니다."),
	NOT_EXIST_CATEGORY(HttpStatus.BAD_REQUEST, "2001", "존재하지 않는 카테고리 입니다."),
	REGISTERED_CATEGORY_POST(HttpStatus.BAD_REQUEST, "2002", "해당 카테고리로 존재하는 글이 존재합니다."),

	//Tag : 3000 : 3999
	DUPLICATION_TAG(HttpStatus.BAD_REQUEST, "3000", "이미 생성된 태그 입니다."),
	NOT_EXIST_TAG(HttpStatus.BAD_REQUEST, "3001", "존재하지 않는 태그 입니다."),
	NOT_EXIST_TAG_LIST(HttpStatus.BAD_REQUEST, "3002", "존재하지 않는 태그가 존재합니다. 태그 목록을 확인해주세요."),

	//Post : 4000 : 4999
	DUPLICATION_POST_TITLE(HttpStatus.BAD_REQUEST, "4000", "중복된 제목 입니다."),
	NOT_EXIST_POST(HttpStatus.BAD_REQUEST, "4001", "글을 찾을 수 없습니다."),

	//Upload : 5000 ~ 5999
	INVALID_FILE(HttpStatus.BAD_REQUEST, "5000", "파일 업로드 실패. 잘못된 파일 입니다."),
	FILE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "5001", "파일 업로드 실패, 재시도 혹은 관리자 문의해주세요."),

	//Stack : 6000 ~ 6999
	ALREADY_EXIST_STACK_SERIES(HttpStatus.BAD_REQUEST, "6000", "이미 존재하는 스택(시리즈) 입니다."),
	INVALID_STACK_POST_SEQUENCE(HttpStatus.BAD_REQUEST, "6001", "포스팅 순서가 유효하지 않습니다. 1부터 N 까지 중복없이 지정되야 합니다."),
	INVALID_POST_LIST(HttpStatus.BAD_REQUEST, "6002", "글을 찾을 수 없거나, 중복된 포스팅이 포함되었습니다. 입력값 오류"),
	ALREADY_REGISTER_POST_STACK_SERIES(HttpStatus.BAD_REQUEST, "6003", "이미 스택(시리즈)에 등록된 글이 존재합니다. 확인해주세요."),

	//Common: 9000 ~ 9999
	NOT_FOUND_URL(HttpStatus.NOT_FOUND, "9001", "요청하신 URL 을 찾을 수 없습니다."),
	NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "9002", "요청 메서드를 찾을 수 없습니다."),
	VALIDATION_FAIL_ERROR(HttpStatus.BAD_REQUEST, "9003", ""),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "9004", "올바르지 않은 입력값입니다."),
	HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "9007", "요청이 거부되었습니다."),
	METHOD_ARGUMENT_TYPE_MISS_MATCH(HttpStatus.BAD_REQUEST, "9008", "요청 파라미터 타입 불일치. API 문서 확인해주세요."),
	INVALID_PAGINATION_PARAMETER(HttpStatus.BAD_REQUEST, "9009", "올바르지 않은 페이지 네이션 파라미터 요청입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9999", "서버 내부 오류 발생했습니다");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
