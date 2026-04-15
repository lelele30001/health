-- 插入用户行为数据用于测试协同过滤推荐

-- 用户1的行为
INSERT INTO user_behavior (user_id, recipe_id, behavior_type, create_time) VALUES
(1, 1, 'like', NOW()),
(1, 2, 'collect', NOW()),
(1, 3, 'like', NOW()),
(1, 4, 'view', NOW()),
(1, 5, 'like', NOW());

-- 用户2的行为
INSERT INTO user_behavior (user_id, recipe_id, behavior_type, create_time) VALUES
(2, 1, 'like', NOW()),
(2, 2, 'like', NOW()),
(2, 4, 'collect', NOW()),
(2, 5, 'view', NOW()),
(2, 6, 'like', NOW()),
(2, 7, 'view', NOW()),
(2, 8, 'like', NOW()),
(2, 9, 'collect', NOW());

-- 用户3的行为
INSERT INTO user_behavior (user_id, recipe_id, behavior_type, create_time) VALUES
(3, 2, 'like', NOW()),
(3, 3, 'collect', NOW()),
(3, 4, 'collect', NOW()),
(3, 7, 'like', NOW()),
(3, 8, 'view', NOW());

-- 用户4的行为
INSERT INTO user_behavior (user_id, recipe_id, behavior_type, create_time) VALUES
(4, 1, 'like', NOW()),
(4, 3, 'collect', NOW()),
(4, 5, 'like', NOW()),
(4, 6, 'collect', NOW()),
(4, 9, 'like', NOW());

-- 用户5的行为
INSERT INTO user_behavior (user_id, recipe_id, behavior_type, create_time) VALUES
(5, 2, 'like', NOW()),
(5, 5, 'collect', NOW()),
(5, 7, 'like', NOW()),
(5, 8, 'collect', NOW()),
(5, 10, 'like', NOW());

-- 查看插入的行为数据
SELECT * FROM user_behavior ORDER BY user_id, recipe_id;