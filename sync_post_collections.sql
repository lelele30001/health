-- 同步帖子收藏行为到 storeup 表
-- 这样前端"我的收藏"页面就能显示用户收藏的帖子

SET SQL_SAFE_UPDATES = 0;

-- 1. 清空现有的帖子收藏记录
DELETE FROM storeup 
WHERE type = '1' AND tablename = 'forum';

-- 2. 同步 user_post_behavior 中的收藏行为到 storeup 表
INSERT INTO storeup (userid, refid, tablename, name, picture, type, addtime)
SELECT 
    upb.user_id as userid,
    upb.post_id as refid,
    'forum' as tablename,
    f.title as name,
    '' as picture, -- 帖子没有图片字段，设为空
    '1' as type, -- 1表示收藏
    NOW() as addtime
FROM 
    user_post_behavior upb
JOIN 
    forum f ON upb.post_id = f.id
WHERE 
    upb.behavior_type = 'collect' -- 只同步收藏行为
    AND NOT EXISTS (
        SELECT 1 FROM storeup 
        WHERE userid = upb.user_id 
        AND refid = upb.post_id 
        AND tablename = 'forum'
    );

-- 3. 查看同步结果
SELECT 
    s.id,
    s.userid,
    s.refid as post_id,
    s.name as post_title,
    f.username as post_author
FROM 
    storeup s
JOIN 
    forum f ON s.refid = f.id
WHERE 
    s.type = '1' AND s.tablename = 'forum'
ORDER BY 
    s.userid, s.addtime DESC;

-- 4. 统计每个用户的收藏数量
SELECT 
    userid,
    COUNT(*) as collection_count
FROM 
    storeup
WHERE 
    type = '1' AND tablename = 'forum'
GROUP BY 
    userid
ORDER BY 
    collection_count DESC;

SET SQL_SAFE_UPDATES = 1;
