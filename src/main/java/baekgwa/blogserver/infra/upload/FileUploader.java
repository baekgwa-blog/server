package baekgwa.blogserver.infra.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * PackageName : baekgwa.blogserver.infra.upload
 * FileName    : FileUploader
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
public interface FileUploader {

	FileUploadResponse uploadImage(FileType type, MultipartFile file);
}
