/**
 * FileName    : V2.2.0__update_post_tag_category_table.sql
 * Author      : Baekgwa
 * Date        : 2025-11-08
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-03-15     Baekgwa             Embedding 관련 처리로직을 별도 서비스에서 이벤트 기반으로 다루기 때문에 필요없는 table 삭제
**/

DROP TABLE IF EXISTS embedding_failure;
