package baekgwa.blogserver.model.category.projection;

/**
 * PackageName : baekgwa.blogserver.model.category.projection
 * FileName    : CategoryPostCount
 * Author      : Baekgwa
 * Date        : 2025-10-21
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-21     Baekgwa               Initial creation
 */
public record CategoryPostCount(
	Long id,
	String name,
	Long postCount
) {
}
