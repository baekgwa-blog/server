package baekgwa.blogserver.global.exception;

import baekgwa.blogserver.global.response.ErrorCode;
import lombok.Getter;

/**
 * PackageName : baekgwa.blogserver.global.exception
 * FileName    : GlobalException
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@Getter
public class GlobalException extends RuntimeException {

	private final ErrorCode errorCode;

	public GlobalException(final ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
