-- 更新recipe表中的cover_image字段，随机分配本地图片路径
UPDATE recipe 
SET cover_image = CASE 
    WHEN RAND() < 0.25 THEN '/recipe_pic/1.jpg'
    WHEN RAND() < 0.5 THEN '/recipe_pic/2.jpg'
    WHEN RAND() < 0.75 THEN '/recipe_pic/3.jpg'
    ELSE '/recipe_pic/4.jpg'
END;