package baekgwa.blogserver.model.stack.stack.entity;

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
 * PackageName : baekgwa.blogserver.model.stack.stack.entity
 * FileName    : StackEntity
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "stack")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StackEntity extends TemporalEntity {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", nullable = false)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private CategoryEntity category;

	@Builder(access = AccessLevel.PRIVATE)
	private StackEntity(String title, String description, CategoryEntity category) {
		this.title = title;
		this.description = description;
		this.category = category;
	}

	public static StackEntity of(String title, String description, CategoryEntity category) {
		return StackEntity
			.builder()
			.title(title)
			.description(description)
			.category(category)
			.build();
	}
}
