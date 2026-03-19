-- 删除所有用户相关数据
-- 禁用安全更新模式
SET SQL_SAFE_UPDATES = 0;

-- 先删除依赖关系强的表
DELETE FROM storeup;
DELETE FROM user_behavior;
DELETE FROM forum;
DELETE FROM discussshipuxinxi;
DELETE FROM discussshicaixinxi;

-- 删除用户表数据
DELETE FROM yonghu;

-- 重新启用安全更新模式
SET SQL_SAFE_UPDATES = 1;

-- 重置自增ID
ALTER TABLE yonghu AUTO_INCREMENT = 1;
ALTER TABLE forum AUTO_INCREMENT = 1;
ALTER TABLE storeup AUTO_INCREMENT = 1;
ALTER TABLE user_behavior AUTO_INCREMENT = 1;
ALTER TABLE discussshipuxinxi AUTO_INCREMENT = 1;
ALTER TABLE discussshicaixinxi AUTO_INCREMENT = 1;

-- 重新创建30个用户
INSERT INTO yonghu (yonghuzhanghao, yonghumima, yonghuxingming, touxiang, xingbie, shoujihaoma, shenfenzheng, youxiang) VALUES
('user1', '123456', '张三', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user1', '男', '13800138001', '110101199001011234', 'user1@example.com'),
('user2', '123456', '李四', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user2', '女', '13800138002', '110101199001011235', 'user2@example.com'),
('user3', '123456', '王五', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user3', '男', '13800138003', '110101199001011236', 'user3@example.com'),
('user4', '123456', '赵六', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user4', '女', '13800138004', '110101199001011237', 'user4@example.com'),
('user5', '123456', '孙七', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user5', '男', '13800138005', '110101199001011238', 'user5@example.com'),
('user6', '123456', '周八', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user6', '女', '13800138006', '110101199001011239', 'user6@example.com'),
('user7', '123456', '吴九', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user7', '男', '13800138007', '110101199001011240', 'user7@example.com'),
('user8', '123456', '郑十', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user8', '女', '13800138008', '110101199001011241', 'user8@example.com'),
('user9', '123456', '王十一', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user9', '男', '13800138009', '110101199001011242', 'user9@example.com'),
('user10', '123456', '赵十二', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user10', '女', '13800138010', '110101199001011243', 'user10@example.com'),
('user11', '123456', '钱十三', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user11', '男', '13800138011', '110101199001011244', 'user11@example.com'),
('user12', '123456', '孙十四', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user12', '女', '13800138012', '110101199001011245', 'user12@example.com'),
('user13', '123456', '李十五', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user13', '男', '13800138013', '110101199001011246', 'user13@example.com'),
('user14', '123456', '周十六', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user14', '女', '13800138014', '110101199001011247', 'user14@example.com'),
('user15', '123456', '吴十七', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user15', '男', '13800138015', '110101199001011248', 'user15@example.com'),
('user16', '123456', '郑十八', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user16', '女', '13800138016', '110101199001011249', 'user16@example.com'),
('user17', '123456', '王十九', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user17', '男', '13800138017', '110101199001011250', 'user17@example.com'),
('user18', '123456', '赵二十', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user18', '女', '13800138018', '110101199001011251', 'user18@example.com'),
('user19', '123456', '钱二十一', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user19', '男', '13800138019', '110101199001011252', 'user19@example.com'),
('user20', '123456', '孙二十二', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user20', '女', '13800138020', '110101199001011253', 'user20@example.com'),
('user21', '123456', '李二十三', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user21', '男', '13800138021', '110101199001011254', 'user21@example.com'),
('user22', '123456', '周二十四', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user22', '女', '13800138022', '110101199001011255', 'user22@example.com'),
('user23', '123456', '吴二十五', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user23', '男', '13800138023', '110101199001011256', 'user23@example.com'),
('user24', '123456', '郑二十六', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user24', '女', '13800138024', '110101199001011257', 'user24@example.com'),
('user25', '123456', '王二十七', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user25', '男', '13800138025', '110101199001011258', 'user25@example.com'),
('user26', '123456', '赵二十八', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user26', '女', '13800138026', '110101199001011259', 'user26@example.com'),
('user27', '123456', '钱二十九', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user27', '男', '13800138027', '110101199001011260', 'user27@example.com'),
('user28', '123456', '孙三十', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user28', '女', '13800138028', '110101199001011261', 'user28@example.com'),
('user29', '123456', '李三十一', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user29', '男', '13800138029', '110101199001011262', 'user29@example.com'),
('user30', '123456', '周三十二', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user30', '女', '13800138030', '110101199001011263', 'user30@example.com');

-- 验证结果
SELECT COUNT(*) FROM yonghu;
