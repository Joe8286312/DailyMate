-- =============================================
-- DailyMate 数据库更新脚本 - 对齐实体字段（MySQL 5.7 兼容）
-- 执行时间：2026-04-03
-- 说明：尽量保留旧字段，新增/补齐实体所需字段
-- =============================================

USE dailymate;

-- -----------------------------
-- 工具：安全添加列（MySQL 5.7）
-- -----------------------------

-- user 表
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user' AND COLUMN_NAME='is_delete') = 0,
  'ALTER TABLE `user` ADD COLUMN `is_delete` TINYINT DEFAULT 0 COMMENT ''是否删除：0-否，1-是''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user_settings' AND COLUMN_NAME='background_url') <> 'longtext',
  'ALTER TABLE `user_settings` MODIFY COLUMN `background_url` LONGTEXT DEFAULT NULL COMMENT ''背景图 URL / Base64''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user' AND COLUMN_NAME='created_at') = 0,
  'ALTER TABLE `user` ADD COLUMN `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user' AND COLUMN_NAME='updated_at') = 0,
  'ALTER TABLE `user` ADD COLUMN `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 兼容旧字段 create_time/update_time
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user' AND COLUMN_NAME='create_time') > 0,
  'UPDATE `user` SET created_at = create_time WHERE created_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user' AND COLUMN_NAME='update_time') > 0,
  'UPDATE `user` SET updated_at = update_time WHERE updated_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- todo 表
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='date') = 0,
  'ALTER TABLE `todo` ADD COLUMN `date` DATE NULL COMMENT ''待办日期''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='reminder_enabled') = 0,
  'ALTER TABLE `todo` ADD COLUMN `reminder_enabled` TINYINT(1) DEFAULT 0 COMMENT ''是否启用提醒：0-否，1-是''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='reminder_offset') = 0,
  'ALTER TABLE `todo` ADD COLUMN `reminder_offset` INT DEFAULT 0 COMMENT ''提醒提前时间（分钟）''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='reminder_time') = 0,
  'ALTER TABLE `todo` ADD COLUMN `reminder_time` DATETIME DEFAULT NULL COMMENT ''提醒触发时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='is_reminded') = 0,
  'ALTER TABLE `todo` ADD COLUMN `is_reminded` TINYINT(1) DEFAULT 0 COMMENT ''是否已提醒：0-否，1-是''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='is_delete') = 0,
  'ALTER TABLE `todo` ADD COLUMN `is_delete` TINYINT DEFAULT 0 COMMENT ''是否删除：0-否，1-是''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='created_at') = 0,
  'ALTER TABLE `todo` ADD COLUMN `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='updated_at') = 0,
  'ALTER TABLE `todo` ADD COLUMN `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 兼容旧字段 is_deleted/create_time/update_time
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='is_deleted') > 0,
  'UPDATE `todo` SET is_delete = is_deleted WHERE is_delete IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='create_time') > 0,
  'UPDATE `todo` SET created_at = create_time WHERE created_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND COLUMN_NAME='update_time') > 0,
  'UPDATE `todo` SET updated_at = update_time WHERE updated_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- reminder 索引
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='todo' AND INDEX_NAME='idx_reminder') = 0,
  'CREATE INDEX idx_reminder ON `todo` (`reminder_enabled`, `is_reminded`, `reminder_time`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- bill 表
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='remark') = 0,
  'ALTER TABLE `bill` ADD COLUMN `remark` VARCHAR(255) DEFAULT NULL COMMENT ''备注''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='date') = 0,
  'ALTER TABLE `bill` ADD COLUMN `date` DATE NULL COMMENT ''账单日期''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='is_delete') = 0,
  'ALTER TABLE `bill` ADD COLUMN `is_delete` TINYINT DEFAULT 0 COMMENT ''是否删除：0-否，1-是''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='created_at') = 0,
  'ALTER TABLE `bill` ADD COLUMN `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='updated_at') = 0,
  'ALTER TABLE `bill` ADD COLUMN `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 兼容旧字段 bill_time/is_deleted/create_time/update_time
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='bill_time') > 0,
  'UPDATE `bill` SET date = DATE(bill_time) WHERE date IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='is_deleted') > 0,
  'UPDATE `bill` SET is_delete = is_deleted WHERE is_delete IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='create_time') > 0,
  'UPDATE `bill` SET created_at = create_time WHERE created_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='bill' AND COLUMN_NAME='update_time') > 0,
  'UPDATE `bill` SET updated_at = update_time WHERE updated_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- message 表
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='message' AND COLUMN_NAME='related_id') = 0,
  'ALTER TABLE `message` ADD COLUMN `related_id` BIGINT DEFAULT NULL COMMENT ''关联业务 ID''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='message' AND COLUMN_NAME='created_at') = 0,
  'ALTER TABLE `message` ADD COLUMN `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='message' AND COLUMN_NAME='read_at') = 0,
  'ALTER TABLE `message` ADD COLUMN `read_at` DATETIME DEFAULT NULL COMMENT ''阅读时间''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 兼容旧字段 create_time/read_time
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='message' AND COLUMN_NAME='create_time') > 0,
  'UPDATE `message` SET created_at = create_time WHERE created_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='message' AND COLUMN_NAME='read_time') > 0,
  'UPDATE `message` SET read_at = read_time WHERE read_at IS NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- user_settings 表
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user_settings') = 0,
  'CREATE TABLE `user_settings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT ''设置 ID'',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT ''用户 ID'',
    `language` VARCHAR(20) DEFAULT ''zh-CN'' COMMENT ''语言：zh-CN/en-US/ja-JP/ko-KR'',
    `theme` VARCHAR(20) DEFAULT ''light'' COMMENT ''主题：light/dark/blue/green/purple'',
    `background_type` VARCHAR(20) DEFAULT ''color'' COMMENT ''背景类型：color/image'',
    `background_color` VARCHAR(20) DEFAULT ''#f5f7fa'' COMMENT ''背景色'',
    `background_url` LONGTEXT DEFAULT NULL COMMENT ''背景图 URL / Base64'',
    `background_opacity` INT DEFAULT 100 COMMENT ''背景透明度（0-100)'',
    `background_size` VARCHAR(20) DEFAULT ''cover'' COMMENT ''背景尺寸'',
    `background_position` VARCHAR(20) DEFAULT ''center'' COMMENT ''背景位置'',
    `notification_enabled` TINYINT(1) DEFAULT 1 COMMENT ''是否启用通知：0-否，1-是'',
    `sound_enabled` TINYINT(1) DEFAULT 0 COMMENT ''是否启用声音：0-否，1-是'',
    `compact_mode` TINYINT(1) DEFAULT 0 COMMENT ''是否紧凑模式：0-否，1-是'',
    `animation_enabled` TINYINT(1) DEFAULT 1 COMMENT ''是否启用动画：0-否，1-是'',
    `default_priority` TINYINT DEFAULT 2 COMMENT ''默认优先级：1=高，2=中，3=低'',
    `default_reminder_offset` INT DEFAULT 0 COMMENT ''默认提醒时间（分钟)'',
    `default_home_view` VARCHAR(20) DEFAULT ''dashboard'' COMMENT ''首页默认视图：dashboard/todos/bills'',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''用户设置表''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 兼容旧 user_settings 表补齐字段
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user_settings' AND COLUMN_NAME='background_color') = 0,
  'ALTER TABLE `user_settings` ADD COLUMN `background_color` VARCHAR(20) DEFAULT ''#f5f7fa'' COMMENT ''背景色''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user_settings' AND COLUMN_NAME='background_opacity') = 0,
  'ALTER TABLE `user_settings` ADD COLUMN `background_opacity` INT DEFAULT 100 COMMENT ''背景透明度（0-100)''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user_settings' AND COLUMN_NAME='background_size') = 0,
  'ALTER TABLE `user_settings` ADD COLUMN `background_size` VARCHAR(20) DEFAULT ''cover'' COMMENT ''背景尺寸''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='user_settings' AND COLUMN_NAME='background_position') = 0,
  'ALTER TABLE `user_settings` ADD COLUMN `background_position` VARCHAR(20) DEFAULT ''center'' COMMENT ''背景位置''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
