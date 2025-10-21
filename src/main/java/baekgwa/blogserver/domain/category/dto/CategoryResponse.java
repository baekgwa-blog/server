package baekgwa.blogserver.domain.category.dto;

import java.util.List;

import baekgwa.blogserver.model.category.projection.CategoryPostCount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.category.dto
 * FileName    : CategoryResponse
 * Author      : Baekgwa
 * Date        : 2025-06-06
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-06     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryResponse {

	@Getter
	@Builder(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CategoryList {
		private final String name;
		private final Long id;
		private final Long count;

		public static List<CategoryList> from(List<CategoryPostCount> categoryEntityList) {
			return categoryEntityList
				.stream()
				.map(data -> CategoryList
					.builder()
					.name(data.category().getName())
					.id(data.category().getId())
					.count(data.postCount())
					.build())
				.toList();
		}
	}
}
