-- 设置SQL安全更新模式为0，允许删除操作
SET SQL_SAFE_UPDATES = 0;

-- 删除冗余表
DROP TABLE IF EXISTS user_post_behavior;
DROP TABLE IF EXISTS post_recommendation;

-- 重新创建用户行为表（只保留食谱行为）
DROP TABLE IF EXISTS user_behavior;
CREATE TABLE user_behavior (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id bigint NOT NULL COMMENT '用户ID',
    recipe_id bigint NOT NULL COMMENT '食谱ID',
    behavior_type ENUM('like', 'collect', 'dislike') NOT NULL COMMENT '行为类型',
    behavior_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
    FOREIGN KEY (user_id) REFERENCES yonghu (id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_recipe (user_id, recipe_id, behavior_type),
    PRIMARY KEY (id)
);

-- 重新创建食谱推荐表
DROP TABLE IF EXISTS recipe_recommendation;
CREATE TABLE recipe_recommendation (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id bigint NOT NULL COMMENT '用户ID',
    recipe_id bigint NOT NULL COMMENT '食谱ID',
    recommendation_type ENUM('collaborative', 'content-based', 'hybrid') NOT NULL COMMENT '推荐类型',
    score FLOAT NOT NULL COMMENT '推荐分数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES yonghu (id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_recipe (user_id, recipe_id),
    PRIMARY KEY (id)
);

-- 生成用户行为数据（30个用户，每个用户对多个食谱的行为）
INSERT INTO user_behavior (user_id, recipe_id, behavior_type) VALUES
-- 用户1的行为
(1, 1, 'collect'), (1, 2, 'collect'), (1, 3, 'like'), (1, 4, 'dislike'),
-- 用户2的行为
(2, 1, 'collect'), (2, 2, 'like'), (2, 5, 'collect'), (2, 6, 'like'),
-- 用户3的行为
(3, 2, 'collect'), (3, 3, 'collect'), (3, 7, 'like'), (3, 8, 'collect'),
-- 用户4的行为
(4, 1, 'like'), (4, 4, 'collect'), (4, 5, 'like'), (4, 9, 'collect'),
-- 用户5的行为
(5, 2, 'like'), (5, 6, 'collect'), (5, 7, 'collect'), (5, 10, 'like'),
-- 用户6的行为
(6, 3, 'collect'), (6, 4, 'like'), (6, 8, 'like'), (6, 9, 'dislike'),
-- 用户7的行为
(7, 1, 'collect'), (7, 5, 'like'), (7, 7, 'dislike'), (7, 10, 'collect'),
-- 用户8的行为
(8, 2, 'collect'), (8, 6, 'like'), (8, 8, 'collect'), (8, 9, 'like'),
-- 用户9的行为
(9, 3, 'like'), (9, 4, 'collect'), (9, 7, 'collect'), (9, 10, 'like'),
-- 用户10的行为
(10, 1, 'like'), (10, 5, 'collect'), (10, 6, 'collect'), (10, 8, 'dislike'),
-- 用户11的行为
(11, 11, 'collect'), (11, 12, 'like'), (11, 13, 'collect'), (11, 14, 'like'),
-- 用户12的行为
(12, 11, 'like'), (12, 15, 'collect'), (12, 16, 'like'), (12, 17, 'collect'),
-- 用户13的行为
(13, 12, 'collect'), (13, 13, 'like'), (13, 18, 'collect'), (13, 19, 'like'),
-- 用户14的行为
(14, 11, 'collect'), (14, 14, 'like'), (14, 15, 'collect'), (14, 20, 'like'),
-- 用户15的行为
(15, 12, 'like'), (15, 16, 'collect'), (15, 17, 'like'), (15, 21, 'collect'),
-- 用户16的行为
(16, 13, 'like'), (16, 18, 'collect'), (16, 19, 'like'), (16, 22, 'collect'),
-- 用户17的行为
(17, 14, 'collect'), (17, 15, 'like'), (17, 20, 'collect'), (17, 23, 'like'),
-- 用户18的行为
(18, 16, 'like'), (18, 17, 'collect'), (18, 21, 'like'), (18, 24, 'collect'),
-- 用户19的行为
(19, 18, 'collect'), (19, 19, 'like'), (19, 22, 'collect'), (19, 25, 'like'),
-- 用户20的行为
(20, 20, 'like'), (20, 21, 'collect'), (20, 23, 'like'), (20, 26, 'collect'),
-- 用户21的行为
(21, 27, 'collect'), (21, 28, 'like'), (21, 29, 'collect'), (21, 30, 'like'),
-- 用户22的行为
(22, 27, 'like'), (22, 31, 'collect'), (22, 32, 'like'), (22, 33, 'collect'),
-- 用户23的行为
(23, 28, 'collect'), (23, 29, 'like'), (23, 34, 'collect'), (23, 35, 'like'),
-- 用户24的行为
(24, 27, 'collect'), (24, 30, 'like'), (24, 31, 'collect'), (24, 36, 'like'),
-- 用户25的行为
(25, 28, 'like'), (25, 32, 'collect'), (25, 33, 'like'), (25, 37, 'collect'),
-- 用户26的行为
(26, 29, 'like'), (26, 34, 'collect'), (26, 35, 'like'), (26, 38, 'collect'),
-- 用户27的行为
(27, 30, 'collect'), (27, 31, 'like'), (27, 36, 'collect'), (27, 39, 'like'),
-- 用户28的行为
(28, 32, 'like'), (28, 33, 'collect'), (28, 37, 'like'), (28, 40, 'collect'),
-- 用户29的行为
(29, 34, 'collect'), (29, 35, 'like'), (29, 38, 'collect'), (29, 41, 'like'),
-- 用户30的行为
(30, 36, 'like'), (30, 37, 'collect'), (30, 39, 'like'), (30, 40, 'collect');

-- 基于用户收藏行为的协同过滤推荐
INSERT INTO recipe_recommendation (user_id, recipe_id, recommendation_type, score)
SELECT 
    u1.user_id,
    r.recipe_id,
    'collaborative' as recommendation_type,
    COUNT(*) as score
FROM 
    user_behavior u1
JOIN 
    user_behavior u2 ON u1.behavior_type = u2.behavior_type AND u1.behavior_type = 'collect' AND u1.recipe_id = u2.recipe_id AND u1.user_id != u2.user_id
JOIN 
    user_behavior r ON u2.user_id = r.user_id AND r.behavior_type = 'collect'
WHERE 
    NOT EXISTS (
        SELECT 1 FROM user_behavior WHERE user_id = u1.user_id AND recipe_id = r.recipe_id
    )
GROUP BY 
    u1.user_id, r.recipe_id
ORDER BY 
    u1.user_id, score DESC;

-- 基于用户健康目标和饮食禁忌的内容推荐
INSERT INTO recipe_recommendation (user_id, recipe_id, recommendation_type, score)
SELECT 
    up.user_id,
    r.id as recipe_id,
    'content-based' as recommendation_type,
    CASE 
        WHEN up.fitness_goal = '减脂' AND r.total_calories < 500 THEN 0.9
        WHEN up.fitness_goal = '增肌' AND r.total_calories > 300 THEN 0.9
        WHEN up.fitness_goal = '维持' AND r.total_calories BETWEEN 300 AND 600 THEN 0.9
        ELSE 0.5
    END as score
FROM 
    user_profile up
JOIN 
    recipe r ON 1=1
WHERE 
    NOT EXISTS (
        SELECT 1 FROM user_behavior WHERE user_id = up.user_id AND recipe_id = r.id
    )
    AND NOT EXISTS (
        SELECT 1 FROM recipe_recommendation WHERE user_id = up.user_id AND recipe_id = r.id
    );

-- 混合推荐策略：优先协同过滤，内容推荐作为补充
INSERT INTO recipe_recommendation (user_id, recipe_id, recommendation_type, score)
SELECT 
    user_id,
    recipe_id,
    'hybrid' as recommendation_type,
    score * 1.2 as score
FROM 
    recipe_recommendation
WHERE 
    recommendation_type = 'collaborative'
UNION ALL
SELECT 
    user_id,
    recipe_id,
    'hybrid' as recommendation_type,
    score * 0.8 as score
FROM 
    recipe_recommendation
WHERE 
    recommendation_type = 'content-based'
ON DUPLICATE KEY UPDATE
    score = VALUES(score),
    recommendation_type = 'hybrid';

-- 恢复SQL安全更新模式
SET SQL_SAFE_UPDATES = 1;

-- 查询每个用户的推荐结果
SELECT 
    u.id as user_id,
    u.yonghuxingming as username,
    r.id as recipe_id,
    r.title as recipe_name,
    rr.recommendation_type,
    rr.score
FROM 
    recipe_recommendation rr
JOIN 
    yonghu u ON rr.user_id = u.id
JOIN 
    recipe r ON rr.recipe_id = r.id
ORDER BY 
    rr.user_id, rr.score DESC;