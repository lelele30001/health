-- 创建用户对帖子的行为记录表
CREATE TABLE IF NOT EXISTS `user_post_behavior` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `behavior_type` varchar(20) NOT NULL COMMENT '行为类型：like(点赞), dislike(踩), collect(收藏)',
  `behavior_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post_behavior` (`user_id`, `post_id`, `behavior_type`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `user_post_behavior_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户帖子行为记录表';

-- 创建帖子推荐表（用于存储推荐结果）
CREATE TABLE IF NOT EXISTS `post_recommendation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `score` double NOT NULL COMMENT '推荐分数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_score` (`score`),
  CONSTRAINT `post_recommendation_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子推荐表';

-- 生成用户行为数据的SQL语句
-- 假设我们有10个用户，10个帖子
-- 为每个用户生成一些收藏、点赞和踩的行为

-- 临时禁用安全更新模式
SET SQL_SAFE_UPDATES = 0;

-- 清空现有的用户帖子行为数据
DELETE FROM user_post_behavior;

-- 重新启用安全更新模式
SET SQL_SAFE_UPDATES = 1;

-- 生成用户行为数据
INSERT INTO user_post_behavior (user_id, post_id, behavior_type) VALUES
-- 用户31的行为
(31, 1, 'collect'),
(31, 2, 'collect'),
(31, 3, 'like'),
(31, 4, 'dislike'),
-- 用户32的行为
(32, 1, 'collect'),
(32, 2, 'like'),
(32, 5, 'collect'),
(32, 6, 'like'),
-- 用户33的行为
(33, 2, 'collect'),
(33, 3, 'collect'),
(33, 7, 'like'),
(33, 8, 'collect'),
-- 用户34的行为
(34, 1, 'like'),
(34, 4, 'collect'),
(34, 5, 'like'),
(34, 9, 'collect'),
-- 用户35的行为
(35, 2, 'like'),
(35, 6, 'collect'),
(35, 7, 'collect'),
(35, 10, 'like'),
-- 用户36的行为
(36, 3, 'collect'),
(36, 4, 'like'),
(36, 8, 'like'),
(36, 9, 'dislike'),
-- 用户37的行为
(37, 1, 'collect'),
(37, 5, 'like'),
(37, 7, 'dislike'),
(37, 10, 'collect'),
-- 用户38的行为
(38, 2, 'collect'),
(38, 6, 'like'),
(38, 8, 'collect'),
(38, 9, 'like'),
-- 用户39的行为
(39, 3, 'like'),
(39, 4, 'collect'),
(39, 7, 'collect'),
(39, 10, 'like'),
-- 用户40的行为
(40, 1, 'like'),
(40, 5, 'collect'),
(40, 6, 'collect'),
(40, 8, 'dislike');

-- 1. 协同过滤推荐（基于用户收藏行为）
-- 找出与当前用户收藏帖子重合度最高的相似用户
-- 提取相似用户收藏过、但当前用户未收藏的帖子
-- 按相似度排序后优先推荐

-- 协同过滤推荐SQL
SET @user_id = 1; -- 替换为当前用户ID

WITH similar_users AS (
  -- 计算用户相似度
  SELECT 
    ub1.user_id AS other_user_id,
    COUNT(*) AS common_collections,
    (SELECT COUNT(*) FROM user_post_behavior WHERE user_id = @user_id AND behavior_type = 'collect') AS user_collections,
    (SELECT COUNT(*) FROM user_post_behavior WHERE user_id = ub1.user_id AND behavior_type = 'collect') AS other_collections,
    COUNT(*) / SQRT((SELECT COUNT(*) FROM user_post_behavior WHERE user_id = @user_id AND behavior_type = 'collect') * 
                    (SELECT COUNT(*) FROM user_post_behavior WHERE user_id = ub1.user_id AND behavior_type = 'collect')) AS similarity
  FROM user_post_behavior ub1
  JOIN user_post_behavior ub2 ON ub1.post_id = ub2.post_id
  WHERE ub1.user_id != @user_id
    AND ub2.user_id = @user_id
    AND ub1.behavior_type = 'collect'
    AND ub2.behavior_type = 'collect'
  GROUP BY ub1.user_id
  ORDER BY similarity DESC
  LIMIT 10 -- 取前10个最相似的用户
),
recommended_posts AS (
  -- 提取相似用户收藏过但当前用户未收藏的帖子
  SELECT 
    sp.other_user_id,
    ub.post_id,
    f.title,
    f.content,
    f.thumbsup_number,
    f.crazily_number,
    sp.similarity
  FROM similar_users sp
  JOIN user_post_behavior ub ON sp.other_user_id = ub.user_id
  JOIN forum f ON ub.post_id = f.id
  WHERE ub.behavior_type = 'collect'
    AND ub.post_id NOT IN (
      SELECT post_id FROM user_post_behavior 
      WHERE user_id = @user_id AND behavior_type = 'collect'
    )
    AND ub.post_id NOT IN (
      SELECT post_id FROM user_post_behavior 
      WHERE user_id = @user_id AND behavior_type = 'like'
    )
  GROUP BY ub.post_id
  ORDER BY sp.similarity DESC
  LIMIT 10 -- 取前10个推荐帖子
)
SELECT 
  post_id,
  title,
  content,
  thumbsup_number,
  crazily_number,
  similarity AS score
FROM recommended_posts;

-- 2. 内容推荐（基于用户资料的兜底推荐）
-- 当协同过滤推荐结果不足3条时使用
-- 基于用户健身目标、饮食禁忌等匹配

-- 内容推荐SQL
SET @user_id = 1; -- 替换为当前用户ID

-- 获取用户资料
WITH user_profile AS (
  SELECT 
    fitness_goal,
    dietary_restrictions
  FROM user_profile
  WHERE user_id = @user_id
),
-- 过滤用户已收藏、已点赞的帖子
user_interacted_posts AS (
  SELECT post_id 
  FROM user_post_behavior 
  WHERE user_id = @user_id 
  AND behavior_type IN ('collect', 'like')
),
-- 内容推荐结果
content_recommendations AS (
  SELECT 
    f.id AS post_id,
    f.title,
    f.content,
    f.thumbsup_number,
    f.crazily_number,
    -- 简单的匹配分数计算
    CASE 
      WHEN f.content LIKE CONCAT('%', (SELECT fitness_goal FROM user_profile), '%') THEN 1 ELSE 0 END +
    CASE 
      WHEN (SELECT dietary_restrictions FROM user_profile) IS NOT NULL 
      AND f.content NOT LIKE CONCAT('%', (SELECT dietary_restrictions FROM user_profile), '%') THEN 1 ELSE 0 END AS score
  FROM forum f
  WHERE f.id NOT IN (SELECT post_id FROM user_interacted_posts)
  ORDER BY 
    score DESC,
    RAND() -- 随机采样，避免过度热门偏见
  LIMIT 10 -- 取前10个推荐帖子
)
SELECT 
  post_id,
  title,
  content,
  thumbsup_number,
  crazily_number,
  score
FROM content_recommendations;

-- 3. 混合推荐策略
-- 优先使用协同过滤推荐，如果不足3条则使用内容推荐补充

SET @user_id = 1; -- 替换为当前用户ID

WITH cf_recommendations AS (
  -- 协同过滤推荐结果
  SELECT 
    post_id,
    title,
    content,
    thumbsup_number,
    crazily_number,
    similarity AS score,
    'collaborative' AS recommend_type
  FROM (
    WITH similar_users AS (
      SELECT 
        ub1.user_id AS other_user_id,
        COUNT(*) AS common_collections,
        COUNT(*) / SQRT((SELECT COUNT(*) FROM user_post_behavior WHERE user_id = @user_id AND behavior_type = 'collect') * 
                        (SELECT COUNT(*) FROM user_post_behavior WHERE user_id = ub1.user_id AND behavior_type = 'collect')) AS similarity
      FROM user_post_behavior ub1
      JOIN user_post_behavior ub2 ON ub1.post_id = ub2.post_id
      WHERE ub1.user_id != @user_id
        AND ub2.user_id = @user_id
        AND ub1.behavior_type = 'collect'
        AND ub2.behavior_type = 'collect'
      GROUP BY ub1.user_id
      ORDER BY similarity DESC
      LIMIT 10
    ),
    recommended_posts AS (
      SELECT 
        ub.post_id,
        f.title,
        f.content,
        f.thumbsup_number,
        f.crazily_number,
        MAX(sp.similarity) AS similarity
      FROM similar_users sp
      JOIN user_post_behavior ub ON sp.other_user_id = ub.user_id
      JOIN forum f ON ub.post_id = f.id
      WHERE ub.behavior_type = 'collect'
        AND ub.post_id NOT IN (
          SELECT post_id FROM user_post_behavior 
          WHERE user_id = @user_id AND behavior_type = 'collect'
        )
        AND ub.post_id NOT IN (
          SELECT post_id FROM user_post_behavior 
          WHERE user_id = @user_id AND behavior_type = 'like'
        )
      GROUP BY ub.post_id
      ORDER BY similarity DESC
      LIMIT 10
    )
    SELECT * FROM recommended_posts
  ) cf
),
content_recommendations AS (
  -- 内容推荐结果
  SELECT 
    f.id AS post_id,
    f.title,
    f.content,
    f.thumbsup_number,
    f.crazily_number,
    CASE 
      WHEN f.content LIKE CONCAT('%', (SELECT fitness_goal FROM user_profile WHERE user_id = @user_id), '%') THEN 1 ELSE 0 END +
    CASE 
      WHEN (SELECT dietary_restrictions FROM user_profile WHERE user_id = @user_id) IS NOT NULL 
      AND f.content NOT LIKE CONCAT('%', (SELECT dietary_restrictions FROM user_profile WHERE user_id = @user_id), '%') THEN 1 ELSE 0 END AS score,
    'content' AS recommend_type
  FROM forum f
  WHERE f.id NOT IN (
    SELECT post_id FROM user_post_behavior 
    WHERE user_id = @user_id AND behavior_type IN ('collect', 'like')
  )
  ORDER BY 
    score DESC,
    RAND()
  LIMIT 10
),
-- 混合推荐结果
mixed_recommendations AS (
  SELECT * FROM cf_recommendations
  UNION ALL
  SELECT * FROM content_recommendations
  WHERE content_recommendations.post_id NOT IN (
    SELECT post_id FROM cf_recommendations
  )
  ORDER BY 
    CASE recommend_type WHEN 'collaborative' THEN 1 ELSE 2 END,
    score DESC
  LIMIT 10
)
SELECT 
  post_id,
  title,
  content,
  thumbsup_number,
  crazily_number,
  score,
  recommend_type
FROM mixed_recommendations;