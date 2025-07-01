package baekgwa.blogserver.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import baekgwa.blogserver.model.post.post.entity.PostEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.post.dto
 * FileName    : PostResponse
 * Author      : Baekgwa
 * Date        : 2025-06-20
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-20     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponse {

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CreatePostResponse {
		private String slug;

		public static CreatePostResponse from(String slug) {
			return CreatePostResponse.builder().slug(slug).build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetPostDetailResponse {
		private Long id;
		private String title;
		private String content;
		private String description;
		private String thumbnailImage;
		private String slug;
		private List<String> tagList;
		private String category;
		private String author;
		private LocalDateTime createdAt;
		private LocalDateTime modifiedAt;

		public static GetPostDetailResponse of(PostEntity post, List<String> tagList) {
			return GetPostDetailResponse
				.builder()
				.id(post.getId())
				.title(post.getTitle())
				.content(post.getContent())
				.description(post.getDescription())
				.thumbnailImage(post.getThumbnailImage())
				.slug(post.getSlug())
				.tagList(tagList)
				.category(post.getCategory().getName())
				.author("백과")
				.createdAt(post.getCreatedAt())
				.modifiedAt(post.getModifiedAt())
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GetPostResponse {
		private final Long id;
		private final String title;
		private final String description;
		private final String thumbnailImage;
		private final String slug;
		private final Integer viewCount;
		private final List<String> tagList;
		private final String category;
		private final LocalDateTime createdAt;
		private final LocalDateTime modifiedAt;

		public static GetPostResponse of(PostEntity post, List<String> tagList) {
			return GetPostResponse
				.builder()
				.id(post.getId())
				.title(post.getTitle())
				.description(post.getDescription())
				.thumbnailImage(post.getThumbnailImage())
				.slug(post.getSlug())
				.viewCount(post.getViewCount())
				.tagList(tagList)
				.category(post.getCategory().getName())
				.createdAt(post.getCreatedAt())
				.modifiedAt(post.getModifiedAt())
				.build();
		}
	}
}
