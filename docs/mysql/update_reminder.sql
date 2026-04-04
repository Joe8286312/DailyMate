-- =============================================
-- DailyMate 数据库更新脚本 - 添加提醒功能字段
-- 执行时间：2026-04-02
-- 
-- 使用方法：
-- 1. 如果已安装 MySQL 命令行工具：
--    mysql -u root -p dailymate < update_reminder.sql
--
-- 2. 或者使用 MySQL Workbench / Navicat 等工具执行此脚本
-- =============================================

USE dailymate;

-- 为 todo 表添加提醒相关字段
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='reminder_enabled') = 0,
  'ALTER TABLE `todo` ADD COLUMN `reminder_enabled` TINYINT(1) DEFAULT 0 COMMENT ''是否启用提醒：0-否，1-是'' AFTER `finish_time`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='reminder_offset') = 0,
  'ALTER TABLE `todo` ADD COLUMN `reminder_offset` INT DEFAULT 0 COMMENT ''提醒提前时间（分钟）：0=准时，15=提前 15 分钟，60=提前 1 小时'' AFTER `reminder_enabled`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='reminder_time') = 0,
  'ALTER TABLE `todo` ADD COLUMN `reminder_time` DATETIME DEFAULT NULL COMMENT ''提醒触发时间'' AFTER `reminder_offset`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='is_reminded') = 0,
  'ALTER TABLE `todo` ADD COLUMN `is_reminded` TINYINT(1) DEFAULT 0 COMMENT ''是否已提醒：0-否，1-是'' AFTER `reminder_time`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 添加索引以提高查询效率
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND INDEX_NAME='idx_reminder') = 0,
  'CREATE INDEX idx_reminder ON `todo` (`reminder_enabled`, `is_reminded`, `reminder_time`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 验证字段是否添加成功
-- DESCRIBE todo;
