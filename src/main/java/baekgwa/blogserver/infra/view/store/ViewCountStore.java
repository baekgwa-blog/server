package baekgwa.blogserver.infra.view.store;

import java.util.Map;

import baekgwa.blogserver.infra.view.type.ViewDomain;

/**
 * PackageName : baekgwa.blogserver.infra.view.store
 * FileName    : ViewCountStore
 * Author      : Baekgwa
 * Date        : 25. 11. 3.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 3.     Baekgwa               Initial creation
 */
public interface ViewCountStore {

	void incrementViewCount(ViewDomain domain, Long id);

	Map<Long, Long> getAllViewCount(ViewDomain domain);
}
