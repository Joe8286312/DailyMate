-- =============================================
-- DailyMate 数据库更新脚本 - 添加用户设置表
-- 执行时间：2026-04-02
-- 
-- 使用方法：
-- 1. 如果已安装 MySQL 命令行工具：
--    mysql -u root -p dailymate < add_user_settings.sql
--
-- 2. 或者使用 MySQL Workbench / Navicat 等工具执行此脚本
-- =============================================

USE dailymate;

-- 创建用户设置表
CREATE TABLE IF NOT EXISTS `user_settings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设置 ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    `language` VARCHAR(20) DEFAULT 'zh-CN' COMMENT '语言：zh-CN=简体中文，en-US=英语，ja-JP=日语，ko-KR=韩语',
    `theme` VARCHAR(20) DEFAULT 'light' COMMENT '主题：light=明亮，dark=黑暗，blue=蓝色，green=绿色，purple=紫色',
    `background_type` VARCHAR(20) DEFAULT 'color' COMMENT '背景类型：color=纯色，image=图片',
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
    `default_home_view` VARCHAR(20) DEFAULT 'dashboard' COMMENT '首页默认视图：dashboard=仪表盘，todos=待办，bills=账单',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';

-- 验证表是否创建成功
-- DESCRIBE user_settings;
