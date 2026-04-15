-- 更新帖子的营养目标，使其更加多样化

-- 更新部分帖子为高蛋白
UPDATE recipe SET nutrition_tag = '高蛋白' WHERE id IN (1, 3, 5, 7, 9, 11, 13, 15, 17, 19);

-- 更新部分帖子为低碳水
UPDATE recipe SET nutrition_tag = '低碳水' WHERE id IN (2, 4, 6, 8, 10, 12, 14, 16, 18, 20);

-- 更新部分帖子为高纤维
UPDATE recipe SET nutrition_tag = '高纤维' WHERE id IN (21, 23, 25, 27, 29, 31, 33, 35, 37, 39);

-- 更新部分帖子为低脂肪
UPDATE recipe SET nutrition_tag = '低脂肪' WHERE id IN (22, 24, 26, 28, 30, 32, 34, 36, 38, 40);

-- 查看更新后的结果
SELECT id, title, nutrition_tag FROM recipe ORDER BY id;