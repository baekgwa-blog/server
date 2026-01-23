/**
 * FileName    : V2.2.0__update_post_tag_category_table.sql
 * Author      : Baekgwa
 * Date        : 2025-11-08
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-11-08     Baekgwa             성능 최적화 목표, 커버링 인덱스 및 일반 인덱스 추가 설정
 */

# 포스트 글 목록 페이징 시, 사용될 covering index 추가.
# select pte1_0.post_id,te1_0.name from post_tag pte1_0 join tag te1_0 on pte1_0.tag_id=te1_0.id where pte1_0.post_id in (??,??);
CREATE INDEX idx_post_tag_covering ON post_tag (post_id, tag_id);

# 포스트 글 목록 페이징 시, Category 필터링을 위해 활용되기 때문에, index 처리
# 이때, category name 은 유니크 하게 설정되는게 더 맞아, UNIQUE Index 로 처리
ALTER TABLE `category` ADD UNIQUE INDEX `uk_category_name` (`name`);