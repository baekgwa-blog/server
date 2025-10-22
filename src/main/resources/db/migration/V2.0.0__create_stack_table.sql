/**
 * FileName    : V2.0.0__create_stack_table.sql
 * Author      : Baekgwa
 * Date        : 2025-10-22
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-10-22     Baekgwa               Initial creation
 */

CREATE TABLE `stack`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `category_id` BIGINT       NOT NULL,
    `created_at`  DATETIME     NOT NULL,
    `modified_at` DATETIME     NOT NULL,
    PRIMARY KEY `pk_stack_id` (`id`),
    FOREIGN KEY `fk_stack_category_id` (`category_id`) REFERENCES `category` (`id`),
    UNIQUE KEY `uk_stack_title` (`title`)
);

CREATE TABLE `stack_post`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `stack_id`    BIGINT   NOT NULL,
    `post_id`     BIGINT   NOT NULL,
    `sequence`       BIGINT   NOT NULL,
    `created_at`  DATETIME NOT NULL,
    `modified_at` DATETIME NOT NULL,
    PRIMARY KEY `pk_stack_post_id` (`id`),
    FOREIGN KEY `fk_stack_post_stack_id` (`stack_id`) REFERENCES `stack` (`id`) ON DELETE CASCADE,
    FOREIGN KEY `fk_stack_post_post_id` (`post_id`) REFERENCES `post` (`id`),
    UNIQUE KEY `uk_stack_post_stack_id_post_id` (`stack_id`, `post_id`),
    UNIQUE KEY `uk_stack_post_stack_id_order` (`stack_id`, `sequence`)
)