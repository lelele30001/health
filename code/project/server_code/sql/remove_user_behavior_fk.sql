-- 移除user_behavior表的user_id外键约束
ALTER TABLE `user_behavior` DROP FOREIGN KEY `user_behavior_ibfk_1`;

-- 移除recipe_recommendation表的user_id外键约束
ALTER TABLE `recipe_recommendation` DROP FOREIGN KEY `recipe_recommendation_ibfk_1`;
