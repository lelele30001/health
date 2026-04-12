-- 创建用户每日健康记录表
CREATE TABLE IF NOT EXISTS `user_daily_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_date` date NOT NULL COMMENT '记录日期',
  `weight` double NOT NULL COMMENT '体重(kg)',
  `bmi` double NOT NULL COMMENT 'BMI值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `record_date` (`record_date`),
  CONSTRAINT `user_daily_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `yonghu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户每日健康记录表';

-- 修改用户档案表，添加目标体重和目标BMI字段
ALTER TABLE `user_profile` ADD COLUMN `target_weight` double DEFAULT NULL COMMENT '目标体重(kg)' AFTER `daily_calories`;
ALTER TABLE `user_profile` ADD COLUMN `target_bmi` double DEFAULT NULL COMMENT '目标BMI' AFTER `target_weight`;