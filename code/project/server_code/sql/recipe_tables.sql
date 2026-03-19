-- 食谱主表
CREATE TABLE `recipe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) NOT NULL COMMENT '食谱标题',
  `description` text COMMENT '食谱描述',
  `cover_image` varchar(255) COMMENT '封面图片路径',
  `fitness_goal` varchar(50) COMMENT '适配健身目标（增肌/减脂/维持）',
  `dietary_restrictions` varchar(255) COMMENT '饮食禁忌（逗号分隔）',
  `meal_scene` varchar(50) COMMENT '用餐场景（早餐/午餐/晚餐/加餐）',
  `total_calories` double COMMENT '单份总热量',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `status` varchar(50) NOT NULL DEFAULT '已发布' COMMENT '状态（已发布/草稿）',
  `addtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱主表';

-- 食谱食材明细表
CREATE TABLE `recipe_ingredient` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `recipe_id` bigint(20) NOT NULL COMMENT '关联的食谱ID',
  `ingredient_code` varchar(50) NOT NULL COMMENT '食材编码',
  `ingredient_name` varchar(255) NOT NULL COMMENT '食材名称',
  `weight` double NOT NULL COMMENT '食材重量',
  `unit` varchar(20) NOT NULL COMMENT '单位',
  `base_calories` double NOT NULL COMMENT '基础热量',
  `calories` double NOT NULL COMMENT '实际热量',
  PRIMARY KEY (`id`),
  KEY `recipe_id` (`recipe_id`),
  CONSTRAINT `recipe_ingredient_ibfk_1` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱食材明细表';