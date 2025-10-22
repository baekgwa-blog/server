package baekgwa.blogserver.model.stack.post.entity;

import baekgwa.blogserver.model.post.post.entity.PostEntity;
import baekgwa.blogserver.model.stack.stack.entity.StackEntity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.model.stack.post.entity
 * FileName    : StackPostEntity
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
@Table(name = "stack_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StackPostEntity {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stack_id")
	private StackEntity stack;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private PostEntity post;

	private StackPostEntity(StackEntity stack, PostEntity post) {
		this.stack = stack;
		this.post = post;
	}

	public static StackPostEntity of(StackEntity stack, PostEntity post) {
		return new StackPostEntity(stack, post);
	}
}
