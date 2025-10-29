package baekgwa.blogserver.model.stack.stack.dto;

import java.time.LocalDateTime;

import baekgwa.blogserver.model.stack.stack.entity.StackEntity;

/**
 * PackageName : baekgwa.blogserver.model.stack.stack.dto
 * FileName    : StackStatsDto
 * Author      : Baekgwa
 * Date        : 25. 10. 25.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 10. 25.     Baekgwa               Initial creation
 */
public interface StackStatsDto {
	StackEntity getStack();
	Long getPostCount();
	LocalDateTime getLastUpdate();
}