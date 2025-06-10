/**
 * FileName    : V1.0.0__init.sql
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */

CREATE TABLE `category`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255) NOT NULL,
    `created_at`  DATETIME     NOT NULL,
    `modified_at` DATETIME     NOT NULL,
    PRIMARY KEY `pk_category_id` (`id`)
)
