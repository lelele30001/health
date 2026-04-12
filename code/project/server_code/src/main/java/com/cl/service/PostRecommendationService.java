package com.cl.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.RecipeDao;
import com.cl.dao.UserPostBehaviorDao;
import com.cl.dao.UserProfileDao;
import com.cl.entity.RecipeEntity;
import com.cl.entity.UserPostBehaviorEntity;
import com.cl.entity.UserProfileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子推荐服务
 * 实现基于用户行为的帖子个性化推荐
 */
@Service("postRecommendationService")
public class PostRecommendationService {

    @Autowired
    private UserPostBehaviorDao userPostBehaviorDao;

    @Autowired
    private RecipeDao recipeDao;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 行为权重定义
    private final Map<String, Double> behaviorWeights = new HashMap<>();

    public PostRecommendationService() {
        // 初始化行为权重
        behaviorWeights.put("collect", 5.0);   // 收藏：最高权重
        behaviorWeights.put("like", 3.0);       // 点赞：高权重
        behaviorWeights.put("dislike", -10.0);  // 踩：负权重
    }

    /**
     * 检查并创建必要的表结构
     */
    private void checkAndCreateTables() {
        try {
            // 检查 user_post_behavior 表是否存在
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_post_behavior (`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID', `user_id` bigint NOT NULL COMMENT '用户ID', `post_id` bigint NOT NULL COMMENT '帖子ID', `behavior_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行为类型：like(点赞), dislike(踩), collect(收藏)', `behavior_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间', PRIMARY KEY (`id`), UNIQUE KEY `uk_user_post_behavior` (`user_id`, `post_id`, `behavior_type`), KEY `idx_post_id` (`post_id`), KEY `idx_user_id` (`user_id`), CONSTRAINT `user_post_behavior_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `recipe` (`id`) ON DELETE CASCADE, CONSTRAINT `user_post_behavior_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `yonghu` (`id`) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户帖子行为记录表'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            // 检查 recipe_recommendation 表是否存在
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS recipe_recommendation (`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID', `user_id` bigint NOT NULL COMMENT '用户ID', `recipe_id` bigint NOT NULL COMMENT '食谱ID', `recommendation_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推荐类型：collaborative, content-based, hybrid', `score` double NOT NULL COMMENT '推荐分数', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', PRIMARY KEY (`id`), UNIQUE KEY `uk_user_recipe` (`user_id`,`recipe_id`), KEY `idx_user_id` (`user_id`), KEY `idx_score` (`score`), CONSTRAINT `recipe_recommendation_ibfk_1` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE CASCADE, CONSTRAINT `recipe_recommendation_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `yonghu` (`id`) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱推荐表'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成用户的食谱推荐
     */
    @Transactional
    public void generateRecommendations(Long userId) {
        // 检查并创建必要的表结构
        checkAndCreateTables();
        
        // 1. 获取用户帖子行为数据
        List<UserPostBehaviorEntity> userBehaviors = userPostBehaviorDao.getUserBehaviors(userId);
        
        // 2. 计算用户偏好
        Map<Long, Double> userPreferences = calculateUserPreferences(userBehaviors);
        
        // 3. 获取所有帖子
        List<RecipeEntity> allPosts = recipeDao.selectList(null);
        List<Long> postIds = allPosts.stream()
                .map(RecipeEntity::getId)
                .collect(Collectors.toList());
        
        // 4. 计算推荐分数
        Map<Long, Double> recommendationScores = calculateRecommendationScores(userId, userPreferences, postIds);
        
        // 5. 保存推荐（由于我们使用 recipe_recommendation 表，这里需要特殊处理）
        // 注意：这里我们将帖子ID作为食谱ID存储，因为我们没有专门的帖子推荐表
        saveRecommendations(userId, recommendationScores);
    }

    /**
     * 计算用户偏好
     */
    private Map<Long, Double> calculateUserPreferences(List<UserPostBehaviorEntity> userBehaviors) {
        Map<Long, Double> preferences = new HashMap<>();
        
        for (UserPostBehaviorEntity behavior : userBehaviors) {
            Long postId = behavior.getPostId();
            String behaviorType = behavior.getBehaviorType();
            Double weight = behaviorWeights.getOrDefault(behaviorType, 0.0);
            
            preferences.put(postId, preferences.getOrDefault(postId, 0.0) + weight);
        }
        
        return preferences;
    }

    /**
     * 计算推荐分数
     */
    private Map<Long, Double> calculateRecommendationScores(Long userId, Map<Long, Double> userPreferences, List<Long> postIds) {
        Map<Long, Double> scores = new HashMap<>();
        
        // 遍历所有帖子
        for (Long postId : postIds) {
            // 跳过用户已经有行为的帖子
            if (userPreferences.containsKey(postId)) {
                continue;
            }
            
            // 计算推荐分数
            double score = calculatePostScore(userId, postId, userPreferences);
            
            // 包含所有帖子，不管分数是否大于0
            scores.put(postId, score);
        }
        
        // 按分数排序
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 计算单个帖子的推荐分数
     */
    private double calculatePostScore(Long userId, Long postId, Map<Long, Double> userPreferences) {
        double score = 0.0;
        
        // 1. 获取帖子信息
        RecipeEntity post = recipeDao.selectById(postId);
        if (post == null) {
            return 0.0;
        }
        
        // 2. 基于帖子热度的分数（使用用户行为数据计算）
        // 从user_post_behavior表中获取该帖子的点赞数
        int likeCount = userPostBehaviorDao.countPostBehaviorByType(postId, "like");
        
        double popularityScore = likeCount * 0.3;
        score += popularityScore * 0.2; // 热度权重20%
        
        // 3. 基于用户相似度的协同过滤分数
        // 获取喜欢过相同帖子的用户
        List<UserPostBehaviorEntity> postBehaviors = userPostBehaviorDao.getPostBehaviors(postId);
        Set<Long> similarUsers = postBehaviors.stream()
                .filter(behavior -> !behavior.getUserId().equals(userId))
                .filter(behavior -> "like".equals(behavior.getBehaviorType()) || "collect".equals(behavior.getBehaviorType()))
                .map(UserPostBehaviorEntity::getUserId)
                .collect(Collectors.toSet());
        
        // 计算相似用户的偏好
        double collaborativeScore = 0.0;
        for (Long similarUserId : similarUsers) {
            List<UserPostBehaviorEntity> similarUserBehaviors = userPostBehaviorDao.getUserBehaviors(similarUserId);
            Map<Long, Double> similarUserPreferences = calculateUserPreferences(similarUserBehaviors);
            
            // 计算相似度
            double similarity = calculateSimilarity(userPreferences, similarUserPreferences);
            
            // 根据相似度计算分数
            if (similarity > 0) {
                collaborativeScore += similarity * 1.0;
            }
        }
        
        score += collaborativeScore * 0.5; // 协同过滤权重50%
        
        // 4. 基于内容相似度的分数
        double contentScore = calculateContentScore(userId, post, userPreferences);
        score += contentScore * 0.3; // 内容推荐权重30%
        
        return score;
    }

    /**
     * 计算内容相似度分数
     */
    private double calculateContentScore(Long userId, RecipeEntity post, Map<Long, Double> userPreferences) {
        double score = 0.0;
        
        // 基于帖子标题和描述的TF-IDF相似度计算
        String title = post.getTitle();
        String description = post.getDescription();
        
        if (title == null) title = "";
        if (description == null) description = "";
        
        // 获取用户历史喜欢的帖子
        List<Long> likedPosts = userPreferences.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // 计算内容相似度
        for (Long likedPostId : likedPosts) {
            RecipeEntity likedPost = recipeDao.selectById(likedPostId);
            if (likedPost != null) {
                String likedTitle = likedPost.getTitle() != null ? likedPost.getTitle() : "";
                String likedDescription = likedPost.getDescription() != null ? likedPost.getDescription() : "";
                
                // 简单的关键词匹配
                double titleSimilarity = calculateTextSimilarity(title, likedTitle);
                double descriptionSimilarity = calculateTextSimilarity(description, likedDescription);
                
                score += (titleSimilarity * 0.6 + descriptionSimilarity * 0.4);
            }
        }
        
        return likedPosts.isEmpty() ? 0.0 : score / likedPosts.size();
    }

    /**
     * 计算文本相似度（简单的Jaccard相似度）
     */
    private double calculateTextSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }
        
        // 分词并创建集合（支持中文分词）
        Set<String> set1 = tokenize(text1);
        Set<String> set2 = tokenize(text2);
        
        // 计算交集和并集
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        if (union.isEmpty()) {
            return 0.0;
        }
        
        return (double) intersection.size() / union.size();
    }

    /**
     * 中文分词（简化版）
     */
    private Set<String> tokenize(String text) {
        Set<String> tokens = new HashSet<>();
        
        // 去除标点符号和空格
        text = text.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", " ");
        
        // 按空格分割
        String[] words = text.split("\\s+");
        
        for (String word : words) {
            if (word.length() >= 2) { // 只保留长度大于等于2的词
                tokens.add(word.toLowerCase());
            }
            
            // 对中文进行2-3元分词
            if (word.matches("[\\u4e00-\\u9fa5]")) {
                for (int i = 0; i < word.length() - 1; i++) {
                    // 二元分词
                    tokens.add(word.substring(i, i + 2));
                    
                    // 三元分词
                    if (i < word.length() - 2) {
                        tokens.add(word.substring(i, i + 3));
                    }
                }
            }
        }
        
        return tokens;
    }

    /**
     * 计算用户偏好相似度（余弦相似度）
     */
    private double calculateSimilarity(Map<Long, Double> preferences1, Map<Long, Double> preferences2) {
        Set<Long> commonItems = new HashSet<>(preferences1.keySet());
        commonItems.retainAll(preferences2.keySet());
        
        if (commonItems.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Long itemId : commonItems) {
            double pref1 = preferences1.get(itemId);
            double pref2 = preferences2.get(itemId);
            dotProduct += pref1 * pref2;
            norm1 += pref1 * pref1;
            norm2 += pref2 * pref2;
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 保存推荐结果
     */
    private void saveRecommendations(Long userId, Map<Long, Double> scores) {
        // 由于我们使用 recipe_recommendation 表，这里需要直接执行 SQL 语句
        try {
            // 先删除用户的旧推荐
            jdbcTemplate.update("DELETE FROM recipe_recommendation WHERE user_id = ?", userId);
            
            // 批量插入所有推荐（不再限制数量）
            for (Map.Entry<Long, Double> entry : scores.entrySet()) {
                // 插入推荐记录，将帖子ID作为食谱ID存储
                jdbcTemplate.update(
                    "INSERT INTO recipe_recommendation (user_id, recipe_id, recommendation_type, score, create_time, update_time) VALUES (?, ?, ?, ?, NOW(), NOW())",
                    userId, entry.getKey(), 
                    entry.getValue() > 5.0 ? "collaborative" : (entry.getValue() > 2.0 ? "hybrid" : "content-based"),
                    entry.getValue()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户推荐帖子列表
     */
    public Map<String, Object> getRecommendationsWithTotal(Long userId, int limit, int page) {
        // 检查并创建必要的表结构
        checkAndCreateTables();
        
        // 计算偏移量
        int offset = (page - 1) * limit;
        
        Map<String, Object> result = new HashMap<>();
        List<RecipeEntity> recommendations = new ArrayList<>();
        int total = 0;
        
        // 1. 检查用户是否为新用户（没有行为记录）
        List<UserPostBehaviorEntity> userBehaviors = null;
        try {
            userBehaviors = userPostBehaviorDao.getUserBehaviors(userId);
        } catch (Exception e) {
            // 表可能不存在，视为新用户
            userBehaviors = null;
        }
        
        if (userBehaviors == null || userBehaviors.isEmpty()) {
            // 新用户：使用基于用户资料的冷启动推荐
            List<RecipeEntity> allPosts = getColdStartRecommendations(userId, Integer.MAX_VALUE);
            total = allPosts.size();
            // 执行分页
            int start = offset;
            int end = Math.min(offset + limit, allPosts.size());
            if (start < allPosts.size()) {
                recommendations = allPosts.subList(start, end);
            }
        } else {
            // 2. 老用户：从 recipe_recommendation 表中获取推荐列表
            try {
                // 查询总帖子数
                total = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM recipe_recommendation WHERE user_id = ?",
                    Integer.class, userId
                );
            } catch (Exception e) {
                e.printStackTrace();
                total = 0;
            }
            
            List<Long> postIds = new ArrayList<>();
            try {
                // 查询推荐的帖子ID（存储在 recipe_id 字段中），支持分页
                postIds = jdbcTemplate.queryForList(
                    "SELECT recipe_id FROM recipe_recommendation WHERE user_id = ? ORDER BY score DESC LIMIT ? OFFSET ?",
                    Long.class, userId, limit, offset
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (postIds.isEmpty()) {
                // 如果没有推荐，生成新的
                generateRecommendations(userId);
                try {
                    // 重新查询总帖子数
                    total = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM recipe_recommendation WHERE user_id = ?",
                        Integer.class, userId
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    total = 0;
                }
                
                try {
                    // 重新查询推荐的帖子ID，支持分页
                    postIds = jdbcTemplate.queryForList(
                        "SELECT recipe_id FROM recipe_recommendation WHERE user_id = ? ORDER BY score DESC LIMIT ? OFFSET ?",
                        Long.class, userId, limit, offset
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (!postIds.isEmpty()) {
                recommendations = recipeDao.selectBatchIds(postIds);
            }
        }
        
        result.put("data", recommendations);
        result.put("total", total);
        return result;
    }
    
    /**
     * 获取用户推荐帖子列表（支持分页）
     */
    public List<RecipeEntity> getRecommendations(Long userId, int limit, int page) {
        Map<String, Object> result = getRecommendationsWithTotal(userId, limit, page);
        return (List<RecipeEntity>) result.get("data");
    }
    
    /**
     * 获取用户推荐帖子列表（兼容旧接口）
     */
    public List<RecipeEntity> getRecommendations(Long userId, int limit) {
        return getRecommendations(userId, limit, 1);
    }

    /**
     * 冷启动推荐：基于用户资料推荐最适合的食谱
     * 适用于新用户或没有行为记录的用户
     */
    private List<RecipeEntity> getColdStartRecommendations(Long userId, int limit) {
        // 获取用户资料
        UserProfileEntity userProfile = null;
        try {
            userProfile = userProfileDao.getByUserId(userId);
        } catch (Exception e) {
            // 表可能不存在，视为没有用户资料
            userProfile = null;
        }
        
        // 获取所有帖子
        List<RecipeEntity> allPosts = recipeDao.selectList(null);
        if (allPosts == null || allPosts.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 如果没有用户资料，返回最新的帖子
        if (userProfile == null) {
            return allPosts.stream()
                    .sorted((p1, p2) -> p2.getAddtime().compareTo(p1.getAddtime()))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        
        // 基于用户资料计算每个帖子的匹配分数
        Map<RecipeEntity, Double> postScores = new HashMap<>();
        for (RecipeEntity post : allPosts) {
            double score = calculateColdStartScore(post, userProfile);
            postScores.put(post, score);
        }
        
        // 按分数排序并返回前limit个
        return postScores.entrySet().stream()
                .sorted(Map.Entry.<RecipeEntity, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算冷启动推荐分数
     * 基于用户资料（健身目标、饮食禁忌）与帖子的匹配度
     */
    private double calculateColdStartScore(RecipeEntity post, UserProfileEntity userProfile) {
        double score = 0.0;
        
        // 1. 健身目标匹配（权重40%）
        String userFitnessGoal = userProfile.getFitnessGoal();
        String postFitnessGoal = post.getFitnessGoal();
        if (userFitnessGoal != null && postFitnessGoal != null) {
            if (userFitnessGoal.equals(postFitnessGoal)) {
                score += 40.0;
            } else if (postFitnessGoal.contains(userFitnessGoal) || userFitnessGoal.contains(postFitnessGoal)) {
                score += 20.0;
            }
        }
        
        // 2. 饮食禁忌匹配（权重30%）
        String userDietaryRestrictions = userProfile.getDietaryRestrictions();
        String postDietaryRestrictions = post.getDietaryRestrictions();
        if (userDietaryRestrictions != null && postDietaryRestrictions != null) {
            // 检查帖子是否符合用户的饮食禁忌
            String[] userRestrictions = userDietaryRestrictions.split(",");
            boolean hasConflict = false;
            for (String restriction : userRestrictions) {
                if (postDietaryRestrictions.contains(restriction.trim())) {
                    // 帖子包含用户的饮食禁忌，降低分数
                    hasConflict = true;
                    break;
                }
            }
            if (!hasConflict) {
                score += 30.0;
            }
        } else {
            // 用户没有设置饮食禁忌，给予基础分数
            score += 15.0;
        }
        
        // 3. 热量匹配（权重20%）
        Integer userDailyCalories = userProfile.getDailyCalories();
        Double postTotalCalories = post.getTotalCalories();
        if (userDailyCalories != null && postTotalCalories != null && userDailyCalories > 0) {
            // 计算帖子热量占用户每日所需热量的比例
            double calorieRatio = postTotalCalories / userDailyCalories;
            // 理想比例是20-30%（一餐的热量）
            if (calorieRatio >= 0.15 && calorieRatio <= 0.35) {
                score += 20.0;
            } else if (calorieRatio >= 0.1 && calorieRatio <= 0.4) {
                score += 10.0;
            }
        }
        
        // 4. 用餐场景匹配（权重10%）
        String postMealScene = post.getMealScene();
        if (postMealScene != null && !postMealScene.isEmpty()) {
            // 有明确用餐场景的帖子给予基础分数
            score += 10.0;
        }
        
        return score;
    }

    /**
     * 获取推荐分数
     */
    public Double getRecommendationScore(Long userId, Long postId) {
        try {
            // 从 recipe_recommendation 表中获取推荐分数（帖子ID存储在 recipe_id 字段中）
            Double score = jdbcTemplate.queryForObject(
                "SELECT score FROM recipe_recommendation WHERE user_id = ? AND recipe_id = ?",
                Double.class, userId, postId
            );
            return score != null ? score : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * 获取热门帖子推荐（用于新用户冷启动）
     */
    public List<RecipeEntity> getHotPosts(int limit) {
        // 获取点赞数最多的帖子
        List<RecipeEntity> allPosts = recipeDao.selectList(null);
        return allPosts.stream()
                .sorted((p1, p2) -> {
                    // 从user_post_behavior表中获取点赞数
                    int thumbs1 = userPostBehaviorDao.countPostBehaviorByType(p1.getId(), "like");
                    int thumbs2 = userPostBehaviorDao.countPostBehaviorByType(p2.getId(), "like");
                    return Integer.compare(thumbs2, thumbs1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
}