-- 向recipe表添加username字段
ALTER TABLE `recipe` ADD COLUMN `username` VARCHAR(255) COMMENT '用户名' AFTER `user_id`;