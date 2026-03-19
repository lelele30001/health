-- 用户档案表
CREATE TABLE `user_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `height` DOUBLE NOT NULL COMMENT '身高(cm)',
  `weight` DOUBLE NOT NULL COMMENT '体重(kg)',
  `age` INT(11) NOT NULL COMMENT '年龄',
  `gender` VARCHAR(10) NOT NULL COMMENT '性别',
  `fitness_goal` VARCHAR(20) NOT NULL COMMENT '健身目标',
  `dietary_restrictions` VARCHAR(255) DEFAULT NULL COMMENT '饮食禁忌',
  `daily_calories` DOUBLE DEFAULT NULL COMMENT '每日热量需求(kcal)',
  `addtime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `user_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `yonghu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户档案表';