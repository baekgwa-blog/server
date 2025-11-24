package baekgwa.blogserver.global.response;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.response
 * FileName    : SuccessCode
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
public enum SuccessCode {

	//회원
	LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공."),
	LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공."),

	//카테고리
	CREATE_CATEGORY_SUCCESS(HttpStatus.CREATED, "카테고리 생성 완료."),
	DELETE_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 삭제 완료."),

	//태그
	CREATE_TAG_SUCCESS(HttpStatus.CREATED, "태그 생성 완료."),
	DELETE_TAG_SUCCESS(HttpStatus.OK, "태그 삭제 완료."),

	//포스팅
	CREATE_POST_SUCCESS(HttpStatus.CREATED, "포스팅 완료"),
	DELETE_POST_SUCCESS(HttpStatus.OK, "포스트 삭제 완료"),

	//파일 업로드
	UPLOAD_IMAGE_SUCCESS(HttpStatus.CREATED, "이미지 업로드 완료"),

	//스택 시리즈
	CREATE_STACK_SUCCESS(HttpStatus.CREATED, "스택 생성 완료"),
	FIND_RELATIVE_STACK_SUCCESS(HttpStatus.OK, "글 관련 스택 내용 조회 성공"),
	GET_ALL_STACK_SUCCESS(HttpStatus.OK, "모든 스택 내용 조회 성공"),
	GET_STACK_DETAIL_SUCCESS(HttpStatus.OK, "스택 내용 조회 성공"),
	MODIFY_STACK_SUCCESS(HttpStatus.OK, "스택 내용 수정 성공"),
	GET_MODIFY_STACK_INFO_SUCCESS(HttpStatus.OK, "스택 내용 조회 성공"),

	//AI
	EMBEDDING_POST_SUCCESS(HttpStatus.CREATED, "게시글 임베딩 성공"),

	//Common
	REQUEST_SUCCESS(HttpStatus.OK, "요청 응답 성공.");

	private final HttpStatus status;
	private final String message;
}
