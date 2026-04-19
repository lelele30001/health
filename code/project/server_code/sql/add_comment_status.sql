-- 为 comment 表添加审核状态字段
ALTER TABLE `comment` ADD COLUMN `status` VARCHAR(20) DEFAULT 'pending' COMMENT '审核状态（pending:待审核, approved:已通过, locked:已锁定）' AFTER `content`;

-- 为 status 字段添加索引
ALTER TABLE `comment` ADD INDEX `idx_status` (`status`);
