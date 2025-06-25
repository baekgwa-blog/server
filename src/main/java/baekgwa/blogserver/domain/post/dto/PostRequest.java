package baekgwa.blogserver.domain.post.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * PackageName : baekgwa.blogserver.domain.post.dto
 * FileName    : PostRequest
 * Author      : Baekgwa
 * Date        : 2025-06-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-19     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostRequest {

	@With
	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CreatePost {
		@NotBlank(message = "제목은 필수입니다.")
		private final String title;

		@NotBlank(message = "내용은 필수입니다.")
		private final String content;

		@NotBlank(message = "설명은 필수입니다.")
		private final String description;

		private final String thumbnailImage;

		@NotEmpty(message = "태그는 최소 1개 이상 선택해야 합니다.")
		private final List<@NotNull(message = "태그 ID는 null일 수 없습니다.") Long> tagIdList;

		@NotNull(message = "카테고리는 필수입니다.")
		private final Long categoryId;

		public static CreatePost of(
			String title,
			String content,
			String description,
			String thumbnailImage,
			List<Long> tagIdList,
			Long categoryId
		) {
			return CreatePost
				.builder()
				.title(title)
				.content(content)
				.description(description)
				.thumbnailImage(thumbnailImage)
				.tagIdList(tagIdList)
				.categoryId(categoryId)
				.build();
		}
	}
}
