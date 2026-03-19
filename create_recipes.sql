-- 为每个用户创建3条食谱帖子
-- 禁用安全更新模式
SET SQL_SAFE_UPDATES = 0;

-- 删除现有数据
DELETE FROM recipe_ingredient;
DELETE FROM recipe;

-- 重置自增ID
ALTER TABLE recipe AUTO_INCREMENT = 1;
ALTER TABLE recipe_ingredient AUTO_INCREMENT = 1;

-- 获取食材总数
SELECT COUNT(*) INTO @ingredient_count FROM foods;

SET @meal_scenes = '["早餐","午餐","晚餐","加餐"]';
SET @fitness_goals = '["增肌","减脂","维持"]';
SET @recipe_titles = '["营养均衡","健康美味","简易家常","健身专用","健康料理","传统膳食"]';
SET @recipe_descriptions = '["这是一道营养均衡的%s，适合%s人群食用。","简单易做的%s，美味又健康。","传统家常%s，味道醇厚。","健身期间的理想%s，低热量高蛋白。","健康料理%s，15分钟即可完成。","营养丰富的%s，适合全家人食用。"]';

DELIMITER $$
CREATE PROCEDURE create_recipes()
BEGIN
  DECLARE user_id INT DEFAULT 1;
  DECLARE recipe_count INT DEFAULT 1;
  DECLARE i INT DEFAULT 0;
  DECLARE j INT DEFAULT 0;
  DECLARE recipe_id BIGINT;
  DECLARE total_calories DOUBLE;
  DECLARE ingredient_count INT;
  DECLARE ingredient_index INT;
  DECLARE ingredient_weight DOUBLE;
  DECLARE ingredient_calories DOUBLE;
  DECLARE meal_scene VARCHAR(50);
  DECLARE fitness_goal VARCHAR(50);
  DECLARE title VARCHAR(255);
  DECLARE description TEXT;
  DECLARE username VARCHAR(255);
  DECLARE tindex INT;
  DECLARE dindex INT;
  
  WHILE user_id <= 30 DO
    SELECT yonghuxingming INTO username FROM yonghu WHERE id = user_id;
    
    SET i = 1;
    WHILE i <= 3 DO
      SET meal_scene = JSON_UNQUOTE(JSON_EXTRACT(@meal_scenes, CONCAT('$[', FLOOR(RAND() * 4), ']')));
      SET fitness_goal = JSON_UNQUOTE(JSON_EXTRACT(@fitness_goals, CONCAT('$[', FLOOR(RAND() * 3), ']')));
      SET tindex = FLOOR(RAND() * 6);
      SET dindex = FLOOR(RAND() * 6);
      
      SET title = CONCAT(
        JSON_UNQUOTE(JSON_EXTRACT(@recipe_titles, CONCAT('$[', tindex, ']'))),
        meal_scene
      );
      
      SET description = JSON_UNQUOTE(JSON_EXTRACT(@recipe_descriptions, CONCAT('$[', dindex, ']')));
      SET description = REPLACE(description, '%s', meal_scene);
      SET description = REPLACE(description, '%s', fitness_goal);
      
      INSERT INTO recipe (title, description, cover_image, fitness_goal, dietary_restrictions, meal_scene, user_id, username, status) 
      VALUES (title, description, CONCAT('https://picsum.photos/400/300?random=', recipe_count), fitness_goal, '无', meal_scene, user_id, username, '已发布');
      
      SET recipe_id = LAST_INSERT_ID();
      SET ingredient_count = FLOOR(RAND() * 2) + 2;
      SET total_calories = 0;
      SET j = 1;
      
      WHILE j <= ingredient_count DO
        -- 随机选择一个食材ID
        SET @random_id = FLOOR(RAND() * @ingredient_count) + 1;
        
        -- 获取该食材的信息
        SELECT code, name, calory, unit INTO @ingredient_code, @ingredient_name, @ingredient_calories, @ingredient_unit 
        FROM foods WHERE id = @random_id;
        
        -- 确保获取到了食材信息
        IF @ingredient_code IS NOT NULL THEN
          SET ingredient_weight = FLOOR(RAND() * 151) + 50;
          SET ingredient_calories = (@ingredient_calories * ingredient_weight) / 100;
          SET total_calories = total_calories + ingredient_calories;
          
          INSERT INTO recipe_ingredient (recipe_id, ingredient_code, ingredient_name, weight, unit, base_calories, calories) 
          VALUES (
            recipe_id, 
            @ingredient_code, 
            @ingredient_name, 
            ingredient_weight, 
            COALESCE(@ingredient_unit, 'g'), 
            @ingredient_calories, 
            ingredient_calories
          );
        END IF;
        
        SET j = j + 1;
      END WHILE;
      
      UPDATE recipe SET total_calories = ROUND(total_calories, 2) WHERE id = recipe_id;
      SET i = i + 1;
      SET recipe_count = recipe_count + 1;
    END WHILE;
    
    SET user_id = user_id + 1;
  END WHILE;
END$$
DELIMITER ;

CALL create_recipes();
DROP PROCEDURE create_recipes;
SET SQL_SAFE_UPDATES = 1;

SELECT COUNT(*) FROM recipe;
SELECT COUNT(*) FROM recipe_ingredient;
SELECT * FROM recipe LIMIT 10;