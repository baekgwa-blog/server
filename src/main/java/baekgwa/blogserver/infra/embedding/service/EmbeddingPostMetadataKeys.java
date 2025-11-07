package baekgwa.blogserver.infra.embedding.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.infra.embedding.service
 * FileName    : EmbeddingPostMetadataKeys
 * Author      : Baekgwa
 * Date        : 25. 11. 7.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 7.     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmbeddingPostMetadataKeys {
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String SLUG = "slug";
	public static final String CATEGORY = "category";
	public static final String TAGS = "tags";
	public static final String DESCRIPTION = "description";
	public static final String CREATED_AT = "created_at";
}
