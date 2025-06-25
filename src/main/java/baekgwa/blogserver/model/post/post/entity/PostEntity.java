package baekgwa.blogserver.model.post.post.entity;

import baekgwa.blogserver.global.entity.TemporalEntity;
import baekgwa.blogserver.model.category.entity.CategoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.model.post.post.entity
 * FileName    : PostEntity
 * Author      : Baekgwa
 * Date        : 2025-06-19
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-19     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "post")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostEntity extends TemporalEntity {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount;

	@Column(name = "thumbnail_image", nullable = true, columnDefinition = "TEXT")
	private String thumbnailImage;

	@Column(name = "slug", nullable = false, unique = true)
	private String slug;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private CategoryEntity category;

	public static PostEntity of(String title, String content, String thumbnailImage, String slug,
		CategoryEntity category) {
		return PostEntity
			.builder()
			.title(title)
			.content(content)
			.viewCount(0)
			.thumbnailImage(thumbnailImage)
			.slug(slug)
			.category(category)
			.build();
	}
}
