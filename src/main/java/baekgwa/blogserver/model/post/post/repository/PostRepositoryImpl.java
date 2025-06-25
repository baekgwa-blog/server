package baekgwa.blogserver.model.post.post.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import baekgwa.blogserver.domain.post.dto.PostResponse;
import baekgwa.blogserver.domain.post.type.PostListSort;
import baekgwa.blogserver.model.post.post.entity.QPostEntity;
import baekgwa.blogserver.model.post.tag.entity.QPostTagEntity;
import baekgwa.blogserver.model.tag.entity.QTagEntity;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.model.post.post.repository
 * FileName    : PostRepositoryImpl
 * Author      : Baekgwa
 * Date        : 2025-06-24
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-24     Baekgwa               Initial creation
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QPostEntity postEntity = QPostEntity.postEntity;
	private static final QPostTagEntity postTagEntity = QPostTagEntity.postTagEntity;
	private static final QTagEntity tagEntity = QTagEntity.tagEntity;

	@Override
	public Page<PostResponse.GetPostResponse> searchPostList(
		@Nullable String keyword,
		@Nullable String category,
		Pageable pageable,
		PostListSort sort
	) {
		// 1. category / keyword where 조건문 생성
		BooleanBuilder whereCondition =
			createWhereCondition(category)
				.and(createKeywordCondition(keyword));

		// 2. sort order 조건 생성
		OrderSpecifier<?> orderSpecifier = createOrderSpecifier(sort);

		// 3. 찾아올 post 의 id list 조회
		List<Long> postIdList = queryFactory
			.select(postEntity.id)
			.from(postEntity)
			.where(whereCondition)
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 3-1. 예외처리) 만약, 찾을 데이터가 없다면 빈 리스트 반환
		if (postIdList.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, 0L);
		}

		// 4. Post Id 로, 필요한 tag id와 name 불러오기
		Map<Long, List<String>> tagMap = queryFactory
			.select(postTagEntity.post.id, tagEntity.name)
			.from(postTagEntity)
			.join(tagEntity).on(postTagEntity.tag.id.eq(tagEntity.id))
			.where(postTagEntity.post.id.in(postIdList))
			.fetch()
			.stream()
			.collect(Collectors.groupingBy(
				tuple -> tuple.get(0, Long.class),
				Collectors.mapping(tuple -> tuple.get(1, String.class), Collectors.toList())
			));

		// 5. 전체 데이터 조회. 후, dto 변환
		List<PostResponse.GetPostResponse> findData = queryFactory
			.selectFrom(postEntity)
			.where(postEntity.id.in(postIdList))
			.orderBy(orderSpecifier)
			.fetch()
			.stream()
			.map(post -> PostResponse.GetPostResponse.of(
				post, tagMap.getOrDefault(post.getId(), List.of())))
			.toList();

		// 6. 전체 item 개수 조회
		Long totalCount = queryFactory
			.select(postEntity.count())
			.from(postEntity)
			.where(whereCondition)
			.fetchOne();

		return new PageImpl<>(findData, pageable, totalCount != null ? totalCount : 0);
	}

	private OrderSpecifier<?> createOrderSpecifier(PostListSort sort) {
		return switch (sort) {
			case LATEST -> new OrderSpecifier<>(Order.DESC, postEntity.createdAt);
			case VIEW -> new OrderSpecifier<>(Order.DESC, postEntity.viewCount);
			case OLDEST -> new OrderSpecifier<>(Order.ASC, postEntity.createdAt);
		};
	}

	private BooleanBuilder createWhereCondition(String category) {
		BooleanBuilder builder = new BooleanBuilder();

		if (StringUtils.hasText(category)) {
			builder.and(postEntity.category.name.eq(category));
		}

		return builder;
	}

	/**
	 * 키워드 검색 조건 생성
	 * target : title
	 * @param keyword
	 * @return BooleanBuilder
	 */
	private BooleanBuilder createKeywordCondition(String keyword) {
		BooleanBuilder builder = new BooleanBuilder();

		if (StringUtils.hasText(keyword)) {
			builder.and(postEntity.title.containsIgnoreCase(keyword));
		}

		return builder;
	}
}
