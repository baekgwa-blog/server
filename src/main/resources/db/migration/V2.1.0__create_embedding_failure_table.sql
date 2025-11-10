/**
 * FileName    : V2.0.0__create_stack_table.sql
 * Author      : Baekgwa
 * Date        : 2025-11-08
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-11-08     Baekgwa               Initial creation
 */

CREATE TABLE `embedding_failure`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `post_id`       BIGINT       NOT NULL,
    `reason`        VARCHAR(255) NOT NULL,
    `embedding_job` VARCHAR(50)  NOT NULL,
    `created_at`    DATETIME     NOT NULL,
    `modified_at`   DATETIME     NOT NULL,
    PRIMARY KEY `pk_ef_id` (`id`),
    UNIQUE KEY `uk_ef_post_id` (`post_id`)
);