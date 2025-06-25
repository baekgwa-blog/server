/**
 * FileName    : V1.0.0__init.sql
 * Author      : Baekgwa
 * Date        : 2025-06-19
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-19     Baekgwa               Initial creation
 */

CREATE TABLE `post`
(
    `id`              BIGINT           NOT NULL AUTO_INCREMENT,
    `title`           VARCHAR(255)     NOT NULL,
    `content`         TEXT             NOT NULL,
    `view_count`      INTEGER UNSIGNED NOT NULL DEFAULT 0,
    `category_id`     BIGINT           NOT NULL,
    `thumbnail_image` text             NULL,
    `slug`            VARCHAR(255)     NOT NULL,
    `created_at`      DATETIME         NOT NULL,
    `modified_at`     DATETIME         NOT NULL,
    PRIMARY KEY `pk_post_id` (`id`),
#     FOREIGN KEY `fk_post_category_id` (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL,
    FOREIGN KEY `fk_post_category_id` (`category_id`) REFERENCES `category` (`id`),
    UNIQUE KEY `uk_post_slug` (`slug`)
);

CREATE TABLE `post_tag`
(
    `id`      BIGINT NOT NULL AUTO_INCREMENT,
    `post_id` BIGINT NOT NULL,
    `tag_id`  BIGINT NOT NULL,
    PRIMARY KEY `pk_post_tag_id` (`id`),
    FOREIGN KEY `fk_post_id` (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
    FOREIGN KEY `fk_tag_id` (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
);