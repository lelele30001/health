-- 标准化所有帖子的营养标签，确保与发布食谱时的选项一致

-- 将高纤维、低碳水等标签统一为均衡
UPDATE recipe SET nutrition_tag = '均衡' WHERE nutrition_tag NOT IN ('高蛋白', '低卡', '均衡', '高脂');

-- 查看更新后的结果
SELECT id, title, nutrition_tag FROM recipe ORDER BY id;