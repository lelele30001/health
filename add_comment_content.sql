-- 为user_behavior表添加comment_content字段

ALTER TABLE user_behavior ADD COLUMN comment_content VARCHAR(255) COMMENT '评论内容' AFTER behavior_time;

-- 查看修改后的表结构
DESCRIBE user_behavior;