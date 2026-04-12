-- 修复帖子推荐系统
-- 重新创建用户帖子行为表

SET SQL_SAFE_UPDATES = 0;

-- 删除旧的帖子推荐表
DROP TABLE IF EXISTS post_recommendation;

-- 重新创建用户帖子行为表（如果不存在）
CREATE TABLE IF NOT EXISTS user_post_behavior (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `behavior_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行为类型：like(点赞), dislike(踩), collect(收藏)',
  `behavior_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post_behavior` (`user_id`,`post_id`,`behavior_type`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `user_post_behavior_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_post_behavior_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `yonghu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户帖子行为记录表';

-- 重新创建帖子推荐表
CREATE TABLE IF NOT EXISTS post_recommendation (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `recommendation_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推荐类型：collaborative, content-based, hybrid',
  `score` double NOT NULL COMMENT '推荐分数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`,`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_score` (`score`),
  CONSTRAINT `post_recommendation_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum` (`id`) ON DELETE CASCADE,
  CONSTRAINT `post_recommendation_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `yonghu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子推荐表';

-- 生成测试数据：为不同用户生成不同的行为数据
-- 用户1的行为（喜欢健身相关）
INSERT INTO user_post_behavior (user_id, post_id, behavior_type) VALUES
(1, 1, 'like'), (1, 1, 'collect'),
(1, 3, 'like'), (1, 5, 'collect'),
(1, 7, 'like'), (1, 9, 'collect')
ON DUPLICATE KEY UPDATE behavior_time = CURRENT_TIMESTAMP;

-- 用户2的行为（喜欢减脂相关）
INSERT INTO user_post_behavior (user_id, post_id, behavior_type) VALUES
(2, 2, 'like'), (2, 2, 'collect'),
(2, 4, 'like'), (2, 6, 'collect'),
(2, 8, 'like'), (2, 10, 'collect')
ON DUPLICATE KEY UPDATE behavior_time = CURRENT_TIMESTAMP;

-- 用户3的行为（喜欢增肌相关）
INSERT INTO user_post_behavior (user_id, post_id, behavior_type) VALUES
(3, 1, 'like'), (3, 3, 'collect'),
(3, 5, 'like'), (3, 7, 'collect'),
(3, 9, 'like')
ON DUPLICATE KEY UPDATE behavior_time = CURRENT_TIMESTAMP;

-- 用户4的行为（混合偏好）
INSERT INTO user_post_behavior (user_id, post_id, behavior_type) VALUES
(4, 2, 'like'), (4, 4, 'collect'),
(4, 6, 'like'), (4, 8, 'collect')
ON DUPLICATE KEY UPDATE behavior_time = CURRENT_TIMESTAMP;

-- 用户5的行为
INSERT INTO user_post_behavior (user_id, post_id, behavior_type) VALUES
(5, 1, 'collect'), (5, 3, 'like'),
(5, 5, 'collect'), (5, 7, 'like')
ON DUPLICATE KEY UPDATE behavior_time = CURRENT_TIMESTAMP;

SET SQL_SAFE_UPDATES = 1;

-- 查看生成的数据
SELECT user_id, post_id, behavior_type FROM user_post_behavior ORDER BY user_id, post_id;
