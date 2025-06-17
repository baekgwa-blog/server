package baekgwa.blogserver.domain.tag.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.tag.dto
 * FileName    : TagRequest
 * Author      : Baekgwa
 * Date        : 2025-06-16
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-16     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagRequest {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class CreateTag {
		private String name;

		public static TagRequest.CreateTag of(String name) {
			return new TagRequest.CreateTag(name);
		}
	}
}
