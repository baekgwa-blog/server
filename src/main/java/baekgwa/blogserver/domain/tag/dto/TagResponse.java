package baekgwa.blogserver.domain.tag.dto;

import java.util.List;

import baekgwa.blogserver.model.tag.entity.TagEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.tag.dto
 * FileName    : TagResponse
 * Author      : Baekgwa
 * Date        : 2025-06-16
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-16     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagResponse {

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class TagList {
		private final String name;
		private final Long id;

		public static List<TagResponse.TagList> from(List<TagEntity> tagEntityList) {
			return tagEntityList
				.stream()
				.map(e -> TagResponse.TagList
					.builder()
					.name(e.getName())
					.id(e.getId())
					.build())
				.toList();
		}
	}
}
