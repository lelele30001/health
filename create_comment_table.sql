-- 删除user_behavior表中的comment_content列
ALTER TABLE user_behavior DROP COLUMN comment_content;

-- 创建评论表
CREATE TABLE comment (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    post_id BIGINT(20) NOT NULL COMMENT '帖子ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    content TEXT NOT NULL COMMENT '评论内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 查看表结构
DESCRIBE comment;