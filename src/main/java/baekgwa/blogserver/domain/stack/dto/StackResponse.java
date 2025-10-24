package baekgwa.blogserver.domain.stack.dto;

import java.time.LocalDateTime;
import java.util.List;

import baekgwa.blogserver.model.stack.post.entity.StackPostEntity;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.stack.dto
 * FileName    : StackResponse
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackResponse {

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class StackInfo {
		private final Long stackId;
		private final String title;
		private final List<StackPostInfo> stackPostInfoList;

		public static StackInfo of(StackEntity stack, List<StackPostInfo> stackPostInfoList) {
			return StackInfo
				.builder()
				.stackId(stack.getId())
				.title(stack.getTitle())
				.stackPostInfoList(stackPostInfoList)
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class StackDetailInfo {
		private final Long stackId;
		private final String title;
		private final String description;
		private final String category;
		private final String thumbnailImage;
		private final LocalDateTime createdAt;
		private final List<StackPostInfo> stackPostInfoList;

		public static StackDetailInfo of(StackEntity stack, List<StackPostInfo> stackPostInfoList) {
			return StackDetailInfo
				.builder()
				.stackId(stack.getId())
				.title(stack.getTitle())
				.description(stack.getDescription())
				.category(stack.getCategory().getName())
				.thumbnailImage(stack.getThumbnailImage())
				.createdAt(stack.getCreatedAt())
				.stackPostInfoList(stackPostInfoList)
				.build();
		}
	}

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class StackPostInfo {
		private final Long postId;
		private final String title;
		private final String slug;
		private final Long sequence;
		private final Integer viewCount;
		private final LocalDateTime createdAt;
		private final LocalDateTime modifiedAt;

		public static StackPostInfo of(StackPostEntity stackPost) {
			return StackPostInfo
				.builder()
				.postId(stackPost.getPost().getId())
				.title(stackPost.getPost().getTitle())
				.slug(stackPost.getPost().getSlug())
				.sequence(stackPost.getSequence())
				.viewCount(stackPost.getPost().getViewCount())
				.createdAt(stackPost.getCreatedAt())
				.modifiedAt(stackPost.getModifiedAt())
				.build();
		}
	}
}
