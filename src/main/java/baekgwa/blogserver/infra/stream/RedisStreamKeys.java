package baekgwa.blogserver.infra.stream;

import lombok.experimental.UtilityClass;

/**
 * PackageName : baekgwa.blogserver.infra.stream
 * FileName    : RedisStreamKeys
 * Author      : Baekgwa
 * Date        : 2026-03-15
 * Description : Redis Stream 키 및 Consumer Group 이름 상수 모음
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa               Initial creation
 */
@UtilityClass
public final class RedisStreamKeys {

	public static final String POST_EMBEDDING_EVENTS = "post_embedding_events";
	public static final String USER_BEHAVIOR_EVENTS = "user_behavior_events";

	public static final String EMBEDDING_GROUP = "data-pipeline-embedding-group";
	public static final String BEHAVIOR_GROUP = "data-pipeline-behavior-group";
}
