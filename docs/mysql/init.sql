-- DailyMate 数据库初始化脚本（与实体类一致）

-- 创建用户表（如果不存在）
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    `is_delete` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建待办事项表
CREATE TABLE IF NOT EXISTS `todo` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '待办 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT DEFAULT NULL COMMENT '内容',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-未完成，1-已完成',
    `priority` TINYINT DEFAULT 1 COMMENT '优先级：1-低，2-中，3-高',
    `date` DATE NOT NULL COMMENT '待办日期',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '截止时间',
    `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `reminder_enabled` TINYINT(1) DEFAULT 0 COMMENT '是否启用提醒：0-否，1-是',
    `reminder_offset` INT DEFAULT 0 COMMENT '提醒提前时间（分钟）',
    `reminder_time` DATETIME DEFAULT NULL COMMENT '提醒触发时间',
    `is_reminded` TINYINT(1) DEFAULT 0 COMMENT '是否已提醒：0-否，1-是',
    `is_delete` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_user_status` (`user_id`, `status`),
    INDEX `idx_end_time` (`end_time`),
    INDEX `idx_reminder` (`reminder_enabled`, `is_reminded`, `reminder_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';

-- 创建账单表
CREATE TABLE IF NOT EXISTS `bill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账单 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `type` TINYINT NOT NULL COMMENT '类型：1-收入，2-支出',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `category` VARCHAR(50) NOT NULL COMMENT '分类',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `date` DATE NOT NULL COMMENT '账单日期',
    `is_delete` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_user_type` (`user_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- 创建消息表
CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` VARCHAR(1000) DEFAULT NULL COMMENT '内容',
    `type` TINYINT DEFAULT 1 COMMENT '类型：1-系统通知 2-待办提醒 3-账单提醒 4-其他',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-否，1-是',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联业务 ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `read_at` DATETIME DEFAULT NULL COMMENT '阅读时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_user_is_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 创建用户设置表
CREATE TABLE IF NOT EXISTS `user_settings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设置 ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    `language` VARCHAR(20) DEFAULT 'zh-CN' COMMENT '语言：zh-CN/en-US/ja-JP/ko-KR',
    `theme` VARCHAR(20) DEFAULT 'light' COMMENT '主题：light/dark/blue/green/purple',
    `background_type` VARCHAR(20) DEFAULT 'color' COMMENT '背景类型：color/image',
    `background_color` VARCHAR(20) DEFAULT '#f5f7fa' COMMENT '背景色',
    `background_url` LONGTEXT DEFAULT NULL COMMENT '背景图 URL / Base64',
    `background_opacity` INT DEFAULT 100 COMMENT '背景透明度（0-100）',
    `background_size` VARCHAR(20) DEFAULT 'cover' COMMENT '背景尺寸',
    `background_position` VARCHAR(20) DEFAULT 'center' COMMENT '背景位置',
    `notification_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用通知：0-否，1-是',
    `sound_enabled` TINYINT(1) DEFAULT 0 COMMENT '是否启用声音：0-否，1-是',
    `compact_mode` TINYINT(1) DEFAULT 0 COMMENT '是否紧凑模式：0-否，1-是',
    `animation_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用动画：0-否，1-是',
    `default_priority` TINYINT DEFAULT 2 COMMENT '默认优先级：1=高，2=中，3=低',
    `default_reminder_offset` INT DEFAULT 0 COMMENT '默认提醒时间（分钟）',
    `default_home_view` VARCHAR(20) DEFAULT 'dashboard' COMMENT '首页默认视图：dashboard/todos/bills',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';
