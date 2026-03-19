package com.cl.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.RecipeDao;
import com.cl.dao.RecipeRecommendationDao;
import com.cl.dao.UserBehaviorDao;
import com.cl.entity.RecipeEntity;
import com.cl.entity.RecipeRecommendationEntity;
import com.cl.entity.UserBehaviorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("recommendationService")
public class RecommendationService extends ServiceImpl<RecipeRecommendationDao, RecipeRecommendationEntity> {

    @Autowired
    private RecipeRecommendationDao recommendationDao;

    @Autowired
    private UserBehaviorDao userBehaviorDao;

    @Autowired
    private RecipeDao recipeDao;

    // 行为权重定义
    private final Map<String, Double> behaviorWeights = new HashMap<>();

    public RecommendationService() {
        // 初始化行为权重
        behaviorWeights.put("collect", 5.0);   // 收藏：最高权重
        behaviorWeights.put("like", 3.0);       // 点赞：高权重
        behaviorWeights.put("comment", 2.0);    // 评论：中权重
        behaviorWeights.put("dislike", -10.0);  // 踩：负权重
    }

    // 生成用户推荐
    @Transactional
    public void generateRecommendations(Long userId) {
        // 1. 获取用户行为数据
        List<UserBehaviorEntity> userBehaviors = userBehaviorDao.getUserBehaviors(userId);
        
        // 2. 计算用户偏好
        Map<Long, Double> userPreferences = calculateUserPreferences(userBehaviors);
        
        // 3. 获取其他用户的行为数据
        List<RecipeEntity> allRecipes = recipeDao.selectList(null);
        List<Long> recipeIds = allRecipes.stream().map(RecipeEntity::getId).collect(Collectors.toList());
        
        // 4. 计算推荐分数
        Map<Long, Double> recommendationScores = calculateRecommendationScores(userId, userPreferences, recipeIds);
        
        // 5. 删除旧推荐
        recommendationDao.deleteRecommendationsByUserId(userId);
        
        // 6. 保存新推荐
        saveRecommendations(userId, recommendationScores);
    }

    // 计算用户偏好
    private Map<Long, Double> calculateUserPreferences(List<UserBehaviorEntity> userBehaviors) {
        Map<Long, Double> preferences = new HashMap<>();
        
        for (UserBehaviorEntity behavior : userBehaviors) {
            Long recipeId = behavior.getRecipeId();
            String behaviorType = behavior.getBehaviorType();
            Double weight = behaviorWeights.getOrDefault(behaviorType, 0.0);
            
            preferences.put(recipeId, preferences.getOrDefault(recipeId, 0.0) + weight);
        }
        
        return preferences;
    }

    // 计算推荐分数
    private Map<Long, Double> calculateRecommendationScores(Long userId, Map<Long, Double> userPreferences, List<Long> recipeIds) {
        Map<Long, Double> scores = new HashMap<>();
        
        // 遍历所有食谱
        for (Long recipeId : recipeIds) {
            // 跳过用户已经有行为的食谱
            if (userPreferences.containsKey(recipeId)) {
                continue;
            }
            
            // 计算推荐分数
            double score = calculateRecipeScore(userId, recipeId, userPreferences);
            
            if (score > 0) {
                scores.put(recipeId, score);
            }
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

    // 计算单个食谱的推荐分数
    private double calculateRecipeScore(Long userId, Long recipeId, Map<Long, Double> userPreferences) {
        // 这里实现简单的协同过滤算法
        // 1. 获取喜欢过相同食谱的用户
        List<UserBehaviorEntity> recipeBehaviors = userBehaviorDao.getRecipeBehaviors(recipeId);
        Set<Long> similarUsers = recipeBehaviors.stream()
                .filter(behavior -> !behavior.getUserId().equals(userId))
                .filter(behavior -> "like".equals(behavior.getBehaviorType()) || "collect".equals(behavior.getBehaviorType()))
                .map(UserBehaviorEntity::getUserId)
                .collect(Collectors.toSet());
        
        // 2. 计算相似用户的偏好
        double score = 0.0;
        for (Long similarUserId : similarUsers) {
            List<UserBehaviorEntity> similarUserBehaviors = userBehaviorDao.getUserBehaviors(similarUserId);
            Map<Long, Double> similarUserPreferences = calculateUserPreferences(similarUserBehaviors);
            
            // 计算相似度
            double similarity = calculateSimilarity(userPreferences, similarUserPreferences);
            
            // 根据相似度计算分数
            if (similarity > 0) {
                score += similarity * 1.0; // 基础分数
            }
        }
        
        // 3. 添加流行度分数
        int likeCount = userBehaviorDao.countRecipeBehaviorByType(recipeId, "like");
        int collectCount = userBehaviorDao.countRecipeBehaviorByType(recipeId, "collect");
        int commentCount = userBehaviorDao.countRecipeBehaviorByType(recipeId, "comment");
        int dislikeCount = userBehaviorDao.countRecipeBehaviorByType(recipeId, "dislike");
        
        double popularityScore = (likeCount * 0.3) + (collectCount * 0.5) + (commentCount * 0.2) - (dislikeCount * 0.5);
        score += popularityScore * 0.1; // 流行度权重
        
        return score;
    }

    // 计算用户偏好相似度
    private double calculateSimilarity(Map<Long, Double> preferences1, Map<Long, Double> preferences2) {
        // 计算余弦相似度
        Set<Long> commonRecipes = new HashSet<>(preferences1.keySet());
        commonRecipes.retainAll(preferences2.keySet());
        
        if (commonRecipes.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Long recipeId : commonRecipes) {
            double pref1 = preferences1.get(recipeId);
            double pref2 = preferences2.get(recipeId);
            dotProduct += pref1 * pref2;
            norm1 += pref1 * pref1;
            norm2 += pref2 * pref2;
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 保存推荐结果
    private void saveRecommendations(Long userId, Map<Long, Double> scores) {
        // 只保存前20个推荐
        int count = 0;
        for (Map.Entry<Long, Double> entry : scores.entrySet()) {
            if (count >= 20) {
                break;
            }
            
            RecipeRecommendationEntity recommendation = new RecipeRecommendationEntity();
            recommendation.setUserId(userId);
            recommendation.setRecipeId(entry.getKey());
            recommendation.setScore(entry.getValue());
            
            this.insert(recommendation);
            count++;
        }
    }

    // 获取用户推荐列表
    public List<RecipeEntity> getRecommendations(Long userId, int limit) {
        // 生成推荐（如果需要）
        List<RecipeRecommendationEntity> recommendations = recommendationDao.getRecommendationsByUserId(userId, limit);
        
        if (recommendations.isEmpty()) {
            // 如果没有推荐，生成新的
            generateRecommendations(userId);
            recommendations = recommendationDao.getRecommendationsByUserId(userId, limit);
        }
        
        // 获取食谱详情
        List<Long> recipeIds = recommendations.stream()
                .map(RecipeRecommendationEntity::getRecipeId)
                .collect(Collectors.toList());
        
        if (recipeIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return recipeDao.selectBatchIds(recipeIds);
    }

    // 获取推荐分数
    public Double getRecommendationScore(Long userId, Long recipeId) {
        RecipeRecommendationEntity recommendation = recommendationDao.getRecommendation(userId, recipeId);
        return recommendation != null ? recommendation.getScore() : 0.0;
    }
}