package baekgwa.blogserver.global.environment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.environment
 * FileName    : S3Properties
 * Author      : Baekgwa
 * Date        : 2025-07-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-07-02     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class S3Properties {
	public static final String REGION = "ap-northeast-2";
	public static final String BUCKET = "baekgwa-blog-s3-bucket";
}
