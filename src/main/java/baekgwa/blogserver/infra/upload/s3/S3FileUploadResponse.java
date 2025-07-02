package baekgwa.blogserver.infra.upload.s3;

import baekgwa.blogserver.infra.upload.FileUploadResponse;
import lombok.Getter;

/**
 * PackageName : baekgwa.blogserver.infra.upload.s3
 * FileName    : S3FileUploadResponse
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@Getter
public class S3FileUploadResponse extends FileUploadResponse {
	private final String bucket;

	public S3FileUploadResponse(String fileName, String fileUrl, String bucket) {
		super(fileName, fileUrl);
		this.bucket = bucket;
	}
}
