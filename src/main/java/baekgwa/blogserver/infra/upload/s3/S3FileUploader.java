package baekgwa.blogserver.infra.upload.s3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.blogserver.global.environment.S3Properties;
import baekgwa.blogserver.global.exception.GlobalException;
import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.infra.upload.FileType;
import baekgwa.blogserver.infra.upload.FileUploadResponse;
import baekgwa.blogserver.infra.upload.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * PackageName : baekgwa.blogserver.infra.upload.s3
 * FileName    : S3FileUploader
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileUploader implements FileUploader {

	private final S3Client s3Client;

	@Override
	public FileUploadResponse uploadImage(FileType type, MultipartFile file) {
		if (file.isEmpty()) {
			throw new GlobalException(ErrorCode.INVALID_FILE);
		}

		String key = generateRandomFileKey(type);

		try {
			PutObjectRequest objectRequest = PutObjectRequest
				.builder()
				.bucket(S3Properties.BUCKET)
				.key(key)
				.contentType(resolveContentType(file))
				.contentLength(file.getSize())
				.build();
			s3Client.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GlobalException(ErrorCode.FILE_UPLOAD_FAIL);
		}

		String saveGetUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", S3Properties.BUCKET, S3Properties.REGION,
			key);
		return new S3FileUploadResponse("image", saveGetUrl, S3Properties.BUCKET);
	}

	/**
	 * ContentType 확인 및 fallback 처리
	 */
	private String resolveContentType(MultipartFile file) {
		String contentType = file.getContentType();
		if( contentType == null ) {
			return "application/octet-stream";
		}
		if (contentType.isBlank()) {
			String name = file.getOriginalFilename().toLowerCase();
			String ext = getExtension(name);
			if (ext == null) {
				return "application/octet-stream";
			}
			return switch (ext) {
				case "mp4" -> "video/mp4";
				case "jpg", "jpeg" -> "image/jpeg";
				case "png" -> "image/png";
				case "pdf" -> "application/pdf";
				default -> "application/octet-stream";
			};
		}
		return contentType;
	}

	private String getExtension(String filename) {
		int lastDot = filename.lastIndexOf('.');
		if (lastDot == -1 || lastDot == filename.length() - 1) {
			return null;
		}
		return filename.substring(lastDot + 1);
	}

	private String generateRandomFileKey(FileType type) {
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String uuid = UUID.randomUUID().toString().substring(0, 8);
		return type.getPath() + "/" + time + "_" + uuid;
	}
}
