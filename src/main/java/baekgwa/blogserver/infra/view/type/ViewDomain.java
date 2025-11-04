package baekgwa.blogserver.infra.view.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.infra.view.type
 * FileName    : ViewDomain
 * Author      : Baekgwa
 * Date        : 25. 11. 3.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 3.     Baekgwa               Initial creation
 */
@Getter
@RequiredArgsConstructor
public enum ViewDomain {

	POST("게시글", "post:views");

	private final String description;
	private final String key;
}
