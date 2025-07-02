package baekgwa.blogserver.infra.upload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.infra.upload
 * FileName    : FileType
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum FileType {

	POST_IMAGE("post"),
	;

	private final String path;
}
