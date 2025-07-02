package baekgwa.blogserver.domain.upload;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import baekgwa.blogserver.global.response.ErrorCode;
import baekgwa.blogserver.global.response.SuccessCode;
import baekgwa.blogserver.infra.upload.FileType;
import baekgwa.blogserver.infra.upload.FileUploadResponse;
import baekgwa.blogserver.infra.upload.s3.S3FileUploadResponse;
import baekgwa.blogserver.integration.SpringBootTestSupporter;

/**
 * PackageName : baekgwa.blogserver.domain.upload
 * FileName    : FileUploadControllerTest
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@Transactional
class FileUploadControllerTest extends SpringBootTestSupporter {

	@WithMockUser
	@DisplayName("이미지 파일 업로드")
	@Test
	void imageUpload1() throws Exception {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"fake-image-content".getBytes()
		);
		FileType type = FileType.POST_IMAGE;

		FileUploadResponse stubResponse = new S3FileUploadResponse("test.jpg", "https://test-url",
			"baekgwa-blog-s3-bucket");

		given(fileUploader.uploadImage(eq(type), any(MultipartFile.class))).willReturn(stubResponse);

		// when
		ResultActions perform = mockMvc.perform(multipart("/file/image")
			.file(file)
			.param("type", type.name())
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		// then
		perform.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.message").value(SuccessCode.UPLOAD_IMAGE_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.code").value(String.valueOf(SuccessCode.UPLOAD_IMAGE_SUCCESS.getStatus().value())))
			.andExpect(jsonPath("$.data.fileName").value("test.jpg"))
			.andExpect(jsonPath("$.data.fileUrl").value("https://test-url"));
	}

	@DisplayName("이미지 파일 업로드는 로그인 해야 가능합니다.")
	@Test
	void imageUpload2() throws Exception {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"fake-image-content".getBytes()
		);
		FileType type = FileType.POST_IMAGE;

		FileUploadResponse stubResponse = new S3FileUploadResponse("test.jpg", "https://test-url",
			"baekgwa-blog-s3-bucket");

		given(fileUploader.uploadImage(eq(type), any(MultipartFile.class))).willReturn(stubResponse);

		// when
		ResultActions perform = mockMvc.perform(multipart("/file/image")
			.file(file)
			.param("type", type.name())
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

		// then
		perform.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.message").value(ErrorCode.NEED_LOGIN.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorCode.NEED_LOGIN.getCode()))
			.andExpect(jsonPath("$.data").isEmpty());
	}
}