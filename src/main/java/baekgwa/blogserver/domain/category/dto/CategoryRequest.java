package baekgwa.blogserver.domain.category.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.category.dto
 * FileName    : CategoryRequest
 * Author      : Baekgwa
 * Date        : 2025-06-06
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-06     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryRequest {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class CreateCategory {
		private String name;

		public static CreateCategory of(String name) {
			return new CreateCategory(name);
		}
	}
}
