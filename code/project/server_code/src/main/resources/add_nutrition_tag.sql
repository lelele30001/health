-- 添加营养标签字段到recipe表
ALTER TABLE recipe ADD COLUMN nutrition_tag VARCHAR(255) COMMENT '营养标签：高蛋白 / 低卡 / 均衡 / 高脂';

-- 为现有数据设置默认值为'均衡'
UPDATE recipe SET nutrition_tag = '均衡' WHERE nutrition_tag IS NULL;