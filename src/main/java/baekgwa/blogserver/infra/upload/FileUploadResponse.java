package baekgwa.blogserver.infra.upload;

import lombok.Getter;

/**
 * PackageName : baekgwa.blogserver.infra.upload
 * FileName    : FileUploadResponse
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@Getter
public abstract class FileUploadResponse {
	private final String fileName;
	private final String fileUrl;

	protected FileUploadResponse(String fileName, String fileUrl) {
		this.fileName = fileName;
		this.fileUrl = fileUrl;
	}
}
