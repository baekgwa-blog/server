package baekgwa.blogserver.domain.stack.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.stack.dto
 * FileName    : StackRequest
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackRequest {

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class NewStackSeries {
		@NotBlank(message = "제목은 필수 입력 항목입니다.")
		@Size(max = 255, message = "제목은 최대 255자까지 입력 가능합니다.")
		private String title;

		@NotBlank(message = "설명은 필수 입력 항목입니다.")
		@Size(max = 255, message = "설명은 최대 255자까지 입력 가능합니다.")
		private String description;

		@NotNull(message = "카테고리는 필수입니다.")
		@Positive(message = "유효하지 않은 카테고리 입니다.")
		private Long categoryId;

		@NotEmpty(message = "포스트 목록은 비어 있을 수 없습니다.")
		private List<StackPost> stackPostList;
	}

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class StackPost {
		@Positive(message = "유효하지 않은 포스트 글 입니다. postId(${validatedValue})")
		private Long postId;
		@Min(value = 1, message = "포스트 글 순서는 1이상이어야 합니다.")
		private Long sequence;
	}
}
