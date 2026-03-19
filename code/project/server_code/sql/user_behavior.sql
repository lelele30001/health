-- 用户行为记录表
CREATE TABLE `user_behavior` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `recipe_id` bigint(20) NOT NULL COMMENT '食谱ID',
  `behavior_type` varchar(20) NOT NULL COMMENT '行为类型：like(点赞), dislike(踩), collect(收藏), comment(评论)',
  `behavior_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  `comment_content` text COMMENT '评论内容（仅当behavior_type为comment时有效）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_recipe_behavior` (`user_id`, `recipe_id`, `behavior_type`),
  KEY `idx_recipe_id` (`recipe_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `user_behavior_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为记录表';

-- 食谱推荐表（用于存储推荐结果）
CREATE TABLE `recipe_recommendation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `recipe_id` bigint(20) NOT NULL COMMENT '食谱ID',
  `score` double NOT NULL COMMENT '推荐分数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_recipe` (`user_id`, `recipe_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_score` (`score`),
  CONSTRAINT `recipe_recommendation_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱推荐表';