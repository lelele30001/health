-- 合并用户表和用户档案表的SQL脚本

-- 1. 创建新的用户表，包含所有字段
CREATE TABLE IF NOT EXISTS user_merged (
    id BIGINT PRIMARY KEY,
    yonghuzhanghao VARCHAR(255) NOT NULL,
    yonghumima VARCHAR(255) NOT NULL,
    yonghuxingming VARCHAR(255),
    touxiang VARCHAR(255),
    xingbie VARCHAR(10),
    shoujihaoma VARCHAR(20),
    shenfenzheng VARCHAR(20),
    youxiang VARCHAR(100),
    height DOUBLE,
    weight DOUBLE,
    age INT,
    gender VARCHAR(10),
    fitness_goal VARCHAR(50),
    dietary_restrictions VARCHAR(255),
    daily_calories INT,
    addtime DATETIME
);

-- 2. 从yonghu表和user_profile表迁移数据
INSERT INTO user_merged (
    id, yonghuzhanghao, yonghumima, yonghuxingming, touxiang, xingbie, 
    shoujihaoma, shenfenzheng, youxiang, addtime
) SELECT 
    id, yonghuzhanghao, yonghumima, yonghuxingming, touxiang, xingbie, 
    shoujihaoma, shenfenzheng, youxiang, addtime
FROM yonghu;

-- 3. 更新从user_profile表获取的字段
UPDATE user_merged um
JOIN user_profile up ON um.id = up.user_id
SET 
    um.height = up.height,
    um.weight = up.weight,
    um.age = up.age,
    um.gender = up.gender,
    um.fitness_goal = up.fitness_goal,
    um.dietary_restrictions = up.dietary_restrictions,
    um.daily_calories = up.daily_calories;

-- 4. 重命名原表为备份表
RENAME TABLE yonghu TO yonghu_backup;
RENAME TABLE user_profile TO user_profile_backup;

-- 5. 重命名新表为yonghu
RENAME TABLE user_merged TO yonghu;

-- 6. 创建索引
CREATE INDEX idx_yonghuzhanghao ON yonghu(yonghuzhanghao);
CREATE INDEX idx_addtime ON yonghu(addtime);

-- 7. 查看合并后的表结构
DESCRIBE yonghu;

-- 8. 查看合并后的数据
SELECT * FROM yonghu LIMIT 5;