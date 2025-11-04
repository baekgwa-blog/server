package baekgwa.blogserver.infra.view.updater;

import baekgwa.blogserver.infra.view.type.ViewDomain;

/**
 * PackageName : baekgwa.blogserver.infra.view.producer
 * FileName    : ViewCountUpdater
 * Author      : Baekgwa
 * Date        : 25. 11. 3.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 3.     Baekgwa             특정 도메인의 조회수 증가를 위한 Updater Interface
 */
public interface ViewCountUpdater {

	/**
	 * 특정 itemKey의 조회수를 임시 증가
	 * 실제 증가됨은, Consumer 를 통해 해결
	 * @param id
	 */
	void updateViewCount(ViewDomain viewDomain, Long id, String remoteAddr);
}
