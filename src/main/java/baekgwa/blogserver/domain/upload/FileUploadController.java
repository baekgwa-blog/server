package baekgwa.blogserver.domain.upload;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.blogserver.global.response.BaseResponse;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.infra.upload.FileType;
import baekgwa.blogserver.infra.upload.FileUploadResponse;
import baekgwa.blogserver.infra.upload.FileUploader;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.upload
 * FileName    : FileUploadController
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "File Upload Controller", description = "파일 업로드")
public class FileUploadController {

	private final FileUploader fileUploader;

	@PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<FileUploadResponse> imageUpload(
		@RequestParam("file") MultipartFile file,
		@RequestParam("type") FileType type
	) {
		FileUploadResponse response = fileUploader.uploadImage(type, file);
		return BaseResponse.success(SuccessCode.UPLOAD_IMAGE_SUCCESS, response);
	}
}
