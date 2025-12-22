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


