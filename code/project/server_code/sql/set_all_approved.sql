-- 将所有现有帖子状态设置为已通过
UPDATE `recipe` SET `status` = 'approved' WHERE `status` IS NULL OR `status` != 'approved';

-- 将所有现有评论状态设置为已通过
UPDATE `comment` SET `status` = 'approved' WHERE `status` IS NULL OR `status` != 'approved';
