package baekgwa.blogserver.global.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.response
 * FileName    : PageResponse
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageResponse<T> {
	private final List<T> content;
	private final int pageNo;
	private final int pageSize;
	private final long totalElements;
	private final int totalPages;
	private final boolean isLast;
	private final boolean isFirst;
	private final boolean hasNext;
	private final boolean hasPrevious;

	public static <T> PageResponse<T> of(Page<T> page) {
		return PageResponse
			.<T>builder()
			.content(page.getContent())
			.pageNo(page.getNumber())
			.pageSize(page.getSize())
			.totalElements(page.getTotalElements())
			.totalPages(page.getTotalPages())
			.isLast(page.isLast())
			.isFirst(page.isFirst())
			.hasNext(page.hasNext())
			.hasPrevious(page.hasPrevious())
			.build();
	}
}

