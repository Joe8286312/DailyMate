DROP DATABASE IF EXISTS dailymate;


CREATE DATABASE dailymate
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dailymate;

-- 用户表
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `username` varchar(50) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `email` varchar(100) DEFAULT NULL,
                        `avatar` varchar(255) DEFAULT NULL,
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY (`username`),
                        UNIQUE KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 待办事项表
CREATE TABLE `todo` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `user_id` bigint(20) NOT NULL,
                        `title` varchar(100) NOT NULL,
                        `content` varchar(255) DEFAULT NULL,
                        `priority` tinyint DEFAULT 2,
                        `status` tinyint DEFAULT 0,
                        `date` date NOT NULL,
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        KEY `idx_userid` (`user_id`),
                        FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 账单表
CREATE TABLE `bill` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `user_id` bigint(20) NOT NULL,
                        `type` tinyint NOT NULL,
                        `category` varchar(50) NOT NULL,
                        `amount` decimal(10,2) NOT NULL,
                        `remark` varchar(255) DEFAULT NULL,
                        `date` date NOT NULL,
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        KEY `idx_userid` (`user_id`),
                        FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE user ADD COLUMN is_delete TINYINT(1) DEFAULT 0;
ALTER TABLE todo ADD COLUMN is_delete TINYINT(1) DEFAULT 0;
ALTER TABLE bill ADD COLUMN is_delete TINYINT(1) DEFAULT 0;


ALTER TABLE todo
    ADD COLUMN start_time DATETIME DEFAULT NULL,
  ADD COLUMN end_time DATETIME DEFAULT NULL,
  ADD COLUMN finish_time DATETIME DEFAULT NULL;

-- ===================== 复合索引 =====================
-- 原有的单列 idx_userid 只覆盖 user_id，后续的 is_delete/date/status 过滤都要回表扫描。
-- 将高频查询条件组合成复合索引，让 MySQL 在索引层就完成过滤，减少回表次数。
--
-- 索引列顺序原则：等值列在前，范围列在后（B-Tree 遇到范围条件右侧的列失效）
--   user_id  = 等值（每个接口必带）
--   is_delete= 等值（固定过滤 is_delete=0）
--   date     = 范围（BETWEEN / =）
--   status   = 等值（todo 状态筛选，首页最常用）

-- bill 表：覆盖所有按日期筛选、统计的查询
ALTER TABLE bill
    DROP INDEX idx_userid,
    ADD INDEX idx_bill_user_delete_date (user_id, is_delete, date);

-- todo 表：两个复合索引分别覆盖日期查询和状态查询
ALTER TABLE todo
    DROP INDEX idx_userid,
    ADD INDEX idx_todo_user_delete_date   (user_id, is_delete, date),
    ADD INDEX idx_todo_user_delete_status (user_id, is_delete, status);