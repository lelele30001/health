package com.cl.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.dao.RecipeDao;
import com.cl.dao.StoreupDao;
import com.cl.dao.UserBehaviorDao;

import com.cl.entity.RecipeEntity;
import com.cl.entity.StoreupEntity;
import com.cl.entity.UserBehaviorEntity;
import com.cl.entity.YonghuEntity;
import com.cl.service.YonghuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐分数详情类
 * 用于存储推荐分数的详细信息
 */
class RecommendationScoreDetail {
    private double collaborativeScore; // 协同相似度分数
    private double fitnessGoalScore; // 健身目标匹配分数
    private double nutritionTagScore; // 营养标签匹配分数
    private double aiQuestionScore; // AI问题分析分数
    private double totalScore; // 总分
    private double popularityScore; // 热度分数
    private Long addtime; // 发布时间

    public RecommendationScoreDetail(double collaborativeScore, double fitnessGoalScore, double nutritionTagScore, double aiQuestionScore, double totalScore) {
        this.collaborativeScore = collaborativeScore;
        this.fitnessGoalScore = fitnessGoalScore;
        this.nutritionTagScore = nutritionTagScore;
        this.aiQuestionScore = aiQuestionScore;
        this.totalScore = totalScore;
        this.popularityScore = 0.0;
        this.addtime = 0L;
    }

    public RecommendationScoreDetail(double collaborativeScore, double fitnessGoalScore, double nutritionTagScore, double aiQuestionScore, double totalScore, double popularityScore, Long addtime) {
        this.collaborativeScore = collaborativeScore;
        this.fitnessGoalScore = fitnessGoalScore;
        this.nutritionTagScore = nutritionTagScore;
        this.aiQuestionScore = aiQuestionScore;
        this.totalScore = totalScore;
        this.popularityScore = popularityScore;
        this.addtime = addtime;
    }

    public double getCollaborativeScore() {
        return collaborativeScore;
    }

    public double getFitnessGoalScore() {
        return fitnessGoalScore;
    }

    public double getNutritionTagScore() {
        return nutritionTagScore;
    }

    public double getAiQuestionScore() {
        return aiQuestionScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getPopularityScore() {
        return popularityScore;
    }

    public void setPopularityScore(double popularityScore) {
        this.popularityScore = popularityScore;
    }

    public Long getAddtime() {
        return addtime;
    }

    public void setAddtime(Long addtime) {
        this.addtime = addtime;
    }
}

/**
 * 帖子推荐服务
 * 实现基于用户行为的帖子个性化推荐
 */
@Service("postRecommendationService")
public class PostRecommendationService {

    @Autowired
    private UserBehaviorDao userBehaviorDao;

    @Autowired
    private StoreupDao storeupDao;

    @Autowired
    private RecipeDao recipeDao;

    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 行为权重定义
    private final Map<String, Double> behaviorWeights = new HashMap<>();

    // 健身目标关键词映射
    private final Map<String, String> fitnessGoalKeywords = new HashMap<>();

    public PostRecommendationService() {
        // 初始化行为权重
        behaviorWeights.put("collect", 10.0);   // 收藏：10分
        behaviorWeights.put("like", 5.0);       // 点赞：5分
        behaviorWeights.put("view", 1.0);       // 浏览：1分
        
        // 初始化健身目标关键词
        fitnessGoalKeywords.put("增肌", "增肌");
        fitnessGoalKeywords.put("肌肉", "增肌");
        fitnessGoalKeywords.put("build muscle", "增肌");
        fitnessGoalKeywords.put("减脂", "减脂");
        fitnessGoalKeywords.put("减肥", "减脂");
        fitnessGoalKeywords.put("lose weight", "减脂");
        fitnessGoalKeywords.put("维持", "维持");
        fitnessGoalKeywords.put("保持", "维持");
        fitnessGoalKeywords.put("maintain", "维持");
    }

    /**
     * 检查并创建必要的表结构
     */
    private void checkAndCreateTables() {
        try {
            // 先删除旧表（如果存在），避免外键约束错误
            jdbcTemplate.execute("DROP TABLE IF EXISTS recipe_recommendation");
            // 创建新表，确保外键约束指向正确的表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS recipe_recommendation (`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID', `user_id` bigint NOT NULL COMMENT '用户ID', `recipe_id` bigint NOT NULL COMMENT '帖子ID', `score` double NOT NULL COMMENT '推荐分数', `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', PRIMARY KEY (`id`), UNIQUE KEY `uk_user_recipe` (`user_id`,`recipe_id`), KEY `idx_user_id` (`user_id`), KEY `idx_score` (`score`), CONSTRAINT `recipe_recommendation_ibfk_1` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE CASCADE, CONSTRAINT `recipe_recommendation_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `yonghu` (`id`) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱推荐表'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户的AI问题
     */
    private List<Map<String, Object>> getUserAIQuestions(Long userId) {
        try {
            return jdbcTemplate.queryForList(
                "SELECT question, created_at FROM ai_question WHERE user_id = ? ORDER BY created_at DESC LIMIT 10",
                userId
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 提取AI问题中的健身目标关键词
     */
    private Map<String, Integer> extractFitnessGoalKeywords(List<Map<String, Object>> questions) {
        Map<String, Integer> keywordCount = new HashMap<>();
        
        for (Map<String, Object> question : questions) {
            String questionText = (String) question.get("question");
            if (questionText == null) continue;
            
            // 遍历关键词映射，查找匹配的关键词
            for (Map.Entry<String, String> entry : fitnessGoalKeywords.entrySet()) {
                String keyword = entry.getKey();
                String goal = entry.getValue();
                
                if (questionText.contains(keyword)) {
                    keywordCount.put(goal, keywordCount.getOrDefault(goal, 0) + 1);
                    break; // 每个问题只匹配一个关键词
                }
            }
        }
        
        return keywordCount;
    }

    /**
     * 计算AI问题分析分数
     */
    private double calculateAIQuestionScore(String userGoal, Map<String, Integer> keywordCount, String postGoal) {
        if (keywordCount.isEmpty()) {
            return 0.0;
        }
        
        // 计算总问题数
        int totalQuestions = keywordCount.values().stream().mapToInt(Integer::intValue).sum();
        
        // 确定主导主题
        String dominantGoal = keywordCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(userGoal);
        
        // 计算主导主题占比
        double dominantRatio = (double) keywordCount.get(dominantGoal) / totalQuestions;
        
        // 计算匹配度
        double matchScore = 0.0;
        if (dominantGoal.equals(postGoal)) {
            matchScore = 100.0;
        } else if ("维持".equals(dominantGoal)) {
            matchScore = 90.0;
        } else if ("维持".equals(postGoal)) {
            matchScore = 70.0;
        }
        
        // 计算AI问题分析分数
        return matchScore * dominantRatio;
    }

    /**
     * 调整权重
     */
    private double[] adjustWeights(double goalWeight, double aiWeight, Map<String, Integer> keywordCount) {
        if (keywordCount.isEmpty()) {
            return new double[]{goalWeight, aiWeight};
        }
        
        // 计算总问题数
        int totalQuestions = keywordCount.values().stream().mapToInt(Integer::intValue).sum();
        
        // 确定主导主题占比
        int maxCount = keywordCount.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        double dominantRatio = (double) maxCount / totalQuestions;
        
        // 如果主导主题占比超过60%，调整权重
        if (dominantRatio > 0.6) {
            return new double[]{0.25, 0.25}; // 调整为50:50
        }
        
        return new double[]{goalWeight, aiWeight};
    }

    /**
     * 保存用户的AI问题到数据库
     */
    public void saveUserQuestion(Long userId, String question) {
        // 简化日志输出
        try {
            // 确保用户ID不为null
            if (userId == null) {
                userId = 1L; // 默认为1
            }
            
            // 确保问题内容不为null
            if (question == null || question.trim().isEmpty()) {
                return;
            }
            
            // 检查ai_question表是否存在，不存在则创建
            String createTableSql = "CREATE TABLE IF NOT EXISTS ai_question " +
                "(" +
                "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID', " +
                "  `user_id` bigint NOT NULL COMMENT '用户ID', " +
                "  `question` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题内容', " +
                "  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                "  PRIMARY KEY (`id`), " +
                "  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引' " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI问题表'";
            jdbcTemplate.execute(createTableSql);
            
            // 插入问题到数据库
            String insertSql = "INSERT INTO ai_question (user_id, question, created_at) VALUES (?, ?, NOW())";
            jdbcTemplate.update(insertSql, userId, question);
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
        
        // 1. 获取用户资料
        YonghuEntity userProfile = null;
        try {
            userProfile = yonghuService.selectById(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 如果用户资料为null，创建默认的用户资料
        if (userProfile == null) {
            userProfile = new YonghuEntity();
            userProfile.setId(userId);
            userProfile.setFitnessGoal("维持"); // 默认健身目标为维持
            userProfile.setDietaryRestrictions(""); // 默认无饮食禁忌
        }
        
        // 打印用户信息
        System.out.println("=== 用户信息 ===");
        System.out.println("用户ID: " + userId);
        System.out.println("健身目标: " + userProfile.getFitnessGoal());
        
        // 2. 获取用户帖子行为数据
        List<UserBehaviorEntity> userBehaviors = userBehaviorDao.getUserBehaviors(userId);
        
        // 如果用户没有行为数据，添加一些默认的行为数据用于测试
        if (userBehaviors == null || userBehaviors.isEmpty()) {
            userBehaviors = new ArrayList<>();
            // 添加一些默认的行为数据
            UserBehaviorEntity behavior1 = new UserBehaviorEntity();
            behavior1.setUserId(userId);
            behavior1.setRecipeId(1L);
            behavior1.setBehaviorType("like");
            userBehaviors.add(behavior1);
            UserBehaviorEntity behavior2 = new UserBehaviorEntity();
            behavior2.setUserId(userId);
            behavior2.setRecipeId(2L);
            behavior2.setBehaviorType("collect");
            userBehaviors.add(behavior2);
        }
        
        // 3. 获取用户的AI问题
        List<Map<String, Object>> aiQuestions = getUserAIQuestions(userId);
        
        // 提取AI问题中的健身目标关键词
        Map<String, Integer> keywordCount = extractFitnessGoalKeywords(aiQuestions);
        
        // 4. 获取所有已审核通过的帖子并过滤饮食禁忌
        List<RecipeEntity> allPosts = recipeDao.selectList(
            new EntityWrapper<RecipeEntity>()
                .eq("status", "approved")
        );
        
        // 过滤用户自己发布的帖子
        List<RecipeEntity> filteredPostsByUser = new ArrayList<>();
        for (RecipeEntity post : allPosts) {
            if (post.getUserId() == null || !post.getUserId().equals(userId)) {
                filteredPostsByUser.add(post);
            }
        }
        
        List<RecipeEntity> filteredPosts = filterPostsByDietaryRestrictions(filteredPostsByUser, userProfile.getDietaryRestrictions());
        
        // 5. 计算推荐分数
        Map<Long, RecommendationScoreDetail> recommendationScores = new HashMap<>();
        if (userBehaviors != null && !userBehaviors.isEmpty()) {
            // 有行为用户：协同相似度 30% + 健身目标匹配 30% + 营养标签匹配 20% + AI问题分析 20%
            Map<Long, Double> userPreferences = calculateUserPreferences(userBehaviors);
            recommendationScores = calculateRecommendationScores(userId, userProfile, filteredPosts, userPreferences, keywordCount);
        } else {
            // 无行为冷启动：健身目标匹配 40% + 营养标签匹配 35% + AI问题分析 25%
            recommendationScores = calculateColdStartScores(userProfile, filteredPosts, keywordCount);
        }
        
        // 6. 保存推荐（Top 15）
        saveRecommendations(userId, recommendationScores);
    }

    /**
     * 计算帖子的热度分数（点赞+收藏+浏览的加权总和）
     * 热度权重：收藏:10分, 点赞:5分, 浏览:1分
     */
    private Map<Long, Double> calculatePostPopularity(List<RecipeEntity> posts) {
        Map<Long, Double> popularityScores = new HashMap<>();

        // 初始化所有帖子的热度为0
        for (RecipeEntity post : posts) {
            popularityScores.put(post.getId(), 0.0);
        }

        try {
            // 从 storeup 表获取收藏和点赞数据 (type: 1=收藏, 21=赞, 22=踩)
            List<StoreupEntity> storeups = storeupDao.selectList(null);
            for (StoreupEntity storeup : storeups) {
                Long postId = storeup.getRefid();
                if (postId == null) continue;

                String type = storeup.getType();
                double weight = 0.0;
                if ("1".equals(type)) {
                    weight = 10.0; // 收藏
                } else if ("21".equals(type)) {
                    weight = 5.0; // 赞
                } else if ("22".equals(type)) {
                    weight = -2.0; // 踩（负面权重但不会让热度变负）
                }

                double currentScore = popularityScores.getOrDefault(postId, 0.0);
                popularityScores.put(postId, currentScore + weight);
            }

            // 从 user_post_behavior 表获取行为数据 (like, collect, view)
            List<UserBehaviorEntity> behaviors = userBehaviorDao.selectList(null);
            for (UserBehaviorEntity behavior : behaviors) {
                Long postId = behavior.getRecipeId();
                if (postId == null) continue;

                String behaviorType = behavior.getBehaviorType();
                double weight = 0.0;
                if ("like".equals(behaviorType)) {
                    weight = 5.0;
                } else if ("collect".equals(behaviorType)) {
                    weight = 10.0;
                } else if ("view".equals(behaviorType)) {
                    weight = 1.0;
                }

                double currentScore = popularityScores.getOrDefault(postId, 0.0);
                popularityScores.put(postId, currentScore + weight);
            }

            // 确保热度分数不为负数
            for (Long postId : popularityScores.keySet()) {
                if (popularityScores.get(postId) < 0) {
                    popularityScores.put(postId, 0.0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return popularityScores;
    }

    /**
     * 根据饮食禁忌过滤帖子
     */
    private List<RecipeEntity> filterPostsByDietaryRestrictions(List<RecipeEntity> posts, String forbiddenFoods) {
        // 如果用户没有饮食禁忌，直接返回所有帖子
        if (forbiddenFoods == null || forbiddenFoods.trim().isEmpty() || "无".equals(forbiddenFoods.trim())) {
            return posts;
        }
        
        // 有饮食禁忌，进行过滤
        String[] restrictions = forbiddenFoods.split(",");
        
        List<RecipeEntity> filteredPosts = new ArrayList<>();
        for (RecipeEntity post : posts) {
            String tabooTags = post.getDietaryRestrictions();
            
            // 如果帖子没有禁忌标签，通过
            if (tabooTags == null || tabooTags.trim().isEmpty()) {
                filteredPosts.add(post);
                continue;
            }
            
            // 检查帖子是否包含用户的禁忌食物
            boolean hasRestriction = false;
            for (String restriction : restrictions) {
                String trimmedRestriction = restriction.trim();
                if (!trimmedRestriction.isEmpty() && tabooTags.contains(trimmedRestriction)) {
                    hasRestriction = true;
                    break;
                }
            }
            
            if (!hasRestriction) {
                filteredPosts.add(post);
            }
        }
        
        return filteredPosts;
    }

    /**
     * 计算用户偏好
     */
    private Map<Long, Double> calculateUserPreferences(List<UserBehaviorEntity> userBehaviors) {
        Map<Long, Double> preferences = new HashMap<>();
        
        for (UserBehaviorEntity behavior : userBehaviors) {
            Long postId = behavior.getRecipeId();
            String behaviorType = behavior.getBehaviorType();
            
            // 忽略行为类型为空的记录
            if (behaviorType == null || behaviorType.isEmpty()) {
                continue;
            }
            
            Double weight = behaviorWeights.getOrDefault(behaviorType, 0.0);
            
            // 同一用户对同一帖子只计一次，不重复累加
            if (!preferences.containsKey(postId)) {
                preferences.put(postId, weight);
            }
        }
        
        return preferences;
    }

    /**
     * 计算有行为用户的推荐分数
     */
    private Map<Long, RecommendationScoreDetail> calculateRecommendationScores(Long userId, YonghuEntity userProfile, List<RecipeEntity> posts, Map<Long, Double> userPreferences, Map<String, Integer> keywordCount) {
        Map<Long, RecommendationScoreDetail> scores = new HashMap<>();

        // 计算协同相似度
        Map<Long, Double> collaborativeScores = calculateCollaborativeScores(userId, posts, userPreferences);

        // 归一化协同相似度分数
        Map<Long, Double> normalizedCollaborativeScores = normalizeScores(collaborativeScores);

        // 计算帖子热度分数
        Map<Long, Double> popularityScores = calculatePostPopularity(posts);

        // 调整权重
        double[] weights = adjustWeights(0.3, 0.2, keywordCount);
        double fitnessGoalWeight = weights[0];
        double aiQuestionWeight = weights[1];

        // 遍历所有帖子
        for (RecipeEntity post : posts) {
            Long postId = post.getId();

            // 跳过用户已经有行为的帖子
            if (userPreferences.containsKey(postId)) {
                continue;
            }

            // 跳过用户自己发布的帖子
            if (post.getUserId() != null && post.getUserId().equals(userId)) {
                continue;
            }

            // 计算健身目标匹配分数
            double fitnessGoalScore = calculateFitnessGoalScore(userProfile.getFitnessGoal(), post.getFitnessGoal());

            // 计算营养标签匹配分数
            double nutritionTagScore = calculateNutritionTagScore(userProfile.getFitnessGoal(), post.getNutritionTag());

            // 计算AI问题分析分数
            double aiQuestionScore = calculateAIQuestionScore(userProfile.getFitnessGoal(), keywordCount, post.getFitnessGoal());

            // 加权计算总分
            double collaborativeScore = normalizedCollaborativeScores.getOrDefault(postId, 0.0);
            double totalScore = collaborativeScore * 0.3 + fitnessGoalScore * fitnessGoalWeight + nutritionTagScore * 0.2 + aiQuestionScore * aiQuestionWeight;

            // 获取热度分数
            double popularityScore = popularityScores.getOrDefault(postId, 0.0);

            // 获取发布时间
            Long addtime = post.getAddtime() != null ? post.getAddtime().getTime() : 0L;

            // 保存详细的得分数据
            scores.put(postId, new RecommendationScoreDetail(collaborativeScore, fitnessGoalScore, nutritionTagScore, aiQuestionScore, totalScore, popularityScore, addtime));
        }

        // 按分数排序（总分 > 热度 > 发布时间）
        Map<Long, RecommendationScoreDetail> sortedScores = new LinkedHashMap<>();
        scores.entrySet().stream()
                .sorted((e1, e2) -> {
                    double scoreDiff = Double.compare(e2.getValue().getTotalScore(), e1.getValue().getTotalScore());
                    if (scoreDiff != 0) return (int) scoreDiff;
                    double popularityDiff = Double.compare(e2.getValue().getPopularityScore(), e1.getValue().getPopularityScore());
                    if (popularityDiff != 0) return (int) popularityDiff;
                    return Long.compare(e2.getValue().getAddtime(), e1.getValue().getAddtime());
                })
                .forEach(entry -> sortedScores.put(entry.getKey(), entry.getValue()));

        // 打印排序后的推荐顺序和得分（只保留前5名）
        System.out.println("=== 推荐排序结果（前5名）===");
        int rank = 1;
        int count = 0;
        for (Map.Entry<Long, RecommendationScoreDetail> entry : sortedScores.entrySet()) {
            if (count >= 5) {
                break;
            }
            Long postId = entry.getKey();
            RecommendationScoreDetail scoreDetail = entry.getValue();
            System.out.println("排名 " + rank + ": 帖子ID " + postId + "，总得分: " + scoreDetail.getTotalScore() +
                    " (协同相似度: " + scoreDetail.getCollaborativeScore() +
                    ", 健身目标匹配: " + scoreDetail.getFitnessGoalScore() +
                    ", 营养标签匹配: " + scoreDetail.getNutritionTagScore() +
                    ", AI问题分析: " + scoreDetail.getAiQuestionScore() + ")");
            rank++;
            count++;
        }

        return sortedScores;
    }

    /**
     * 计算冷启动推荐分数
     */
    private Map<Long, RecommendationScoreDetail> calculateColdStartScores(YonghuEntity userProfile, List<RecipeEntity> posts, Map<String, Integer> keywordCount) {
        Map<Long, RecommendationScoreDetail> scores = new HashMap<>();

        // 计算帖子热度分数
        Map<Long, Double> popularityScores = calculatePostPopularity(posts);

        // 调整权重
        double[] weights = adjustWeights(0.4, 0.25, keywordCount);
        double fitnessGoalWeight = weights[0];
        double aiQuestionWeight = weights[1];
        System.out.println("=== 冷启动权重调整 ===");
        System.out.println("健身目标权重: " + fitnessGoalWeight);
        System.out.println("AI问题权重: " + aiQuestionWeight);

        // 遍历所有帖子
        for (RecipeEntity post : posts) {
            // 跳过用户自己发布的帖子
            if (post.getUserId() != null && post.getUserId().equals(userProfile.getId())) {
                continue;
            }

            // 计算健身目标匹配分数
            double fitnessGoalScore = calculateFitnessGoalScore(userProfile.getFitnessGoal(), post.getFitnessGoal());

            // 计算营养标签匹配分数
            double nutritionTagScore = calculateNutritionTagScore(userProfile.getFitnessGoal(), post.getNutritionTag());

            // 计算AI问题分析分数
            double aiQuestionScore = calculateAIQuestionScore(userProfile.getFitnessGoal(), keywordCount, post.getFitnessGoal());

            // 加权计算总分
            double totalScore = fitnessGoalScore * fitnessGoalWeight + nutritionTagScore * 0.35 + aiQuestionScore * aiQuestionWeight;

            // 获取热度分数
            double popularityScore = popularityScores.getOrDefault(post.getId(), 0.0);

            // 获取发布时间
            Long addtime = post.getAddtime() != null ? post.getAddtime().getTime() : 0L;

            // 保存详细的得分数据（冷启动没有协同相似度分数，设为0）
            scores.put(post.getId(), new RecommendationScoreDetail(0.0, fitnessGoalScore, nutritionTagScore, aiQuestionScore, totalScore, popularityScore, addtime));
        }

        // 按分数排序（总分 > 热度 > 发布时间）
        Map<Long, RecommendationScoreDetail> sortedScores = new LinkedHashMap<>();
        scores.entrySet().stream()
                .sorted((e1, e2) -> {
                    double scoreDiff = Double.compare(e2.getValue().getTotalScore(), e1.getValue().getTotalScore());
                    if (scoreDiff != 0) return (int) scoreDiff;
                    double popularityDiff = Double.compare(e2.getValue().getPopularityScore(), e1.getValue().getPopularityScore());
                    if (popularityDiff != 0) return (int) popularityDiff;
                    return Long.compare(e2.getValue().getAddtime(), e1.getValue().getAddtime());
                })
                .forEach(entry -> sortedScores.put(entry.getKey(), entry.getValue()));

        // 打印排序后的推荐顺序和得分（只保留前5名）
        System.out.println("=== 推荐排序结果（前5名）===");
        int rank = 1;
        int count = 0;
        for (Map.Entry<Long, RecommendationScoreDetail> entry : sortedScores.entrySet()) {
            if (count >= 5) {
                break;
            }
            Long postId = entry.getKey();
            RecommendationScoreDetail scoreDetail = entry.getValue();
            System.out.println("排名 " + rank + ": 帖子ID " + postId + "，总得分: " + scoreDetail.getTotalScore() +
                    " (协同相似度: " + scoreDetail.getCollaborativeScore() +
                    ", 健身目标匹配: " + scoreDetail.getFitnessGoalScore() +
                    ", 营养标签匹配: " + scoreDetail.getNutritionTagScore() +
                    ", AI问题分析: " + scoreDetail.getAiQuestionScore() + ")");
            rank++;
            count++;
        }

        return sortedScores;
    }

    /**
     * 计算协同相似度分数
     */
    private Map<Long, Double> calculateCollaborativeScores(Long userId, List<RecipeEntity> posts, Map<Long, Double> userPreferences) {
        Map<Long, Double> scores = new HashMap<>();
        
        try {
            // 获取所有用户的行为数据
            List<UserBehaviorEntity> allUserBehaviors = userBehaviorDao.selectList(null);
            
            // 按用户分组
            Map<Long, List<UserBehaviorEntity>> userBehaviorsMap = new HashMap<>();
            for (UserBehaviorEntity behavior : allUserBehaviors) {
                Long otherUserId = behavior.getUserId();
                if (!userBehaviorsMap.containsKey(otherUserId)) {
                    userBehaviorsMap.put(otherUserId, new ArrayList<>());
                }
                userBehaviorsMap.get(otherUserId).add(behavior);
            }
            
            // 计算与其他用户的相似度
            Map<Long, Double> userSimilarities = new HashMap<>();
            for (Map.Entry<Long, List<UserBehaviorEntity>> entry : userBehaviorsMap.entrySet()) {
                Long otherUserId = entry.getKey();
                if (otherUserId.equals(userId)) {
                    continue;
                }
                
                List<UserBehaviorEntity> otherUserBehaviors = entry.getValue();
                Map<Long, Double> otherUserPreferences = calculateUserPreferences(otherUserBehaviors);
                
                double similarity = calculateSimilarity(userPreferences, otherUserPreferences);
                if (similarity > 0) {
                    userSimilarities.put(otherUserId, similarity);
                }
            }
            
            // 为每个帖子计算协同分数
            for (RecipeEntity post : posts) {
                Long postId = post.getId();
                
                // 跳过用户已经有行为的帖子
                if (userPreferences.containsKey(postId)) {
                    continue;
                }
                
                double score = 0.0;
                for (Map.Entry<Long, Double> entry : userSimilarities.entrySet()) {
                    Long otherUserId = entry.getKey();
                    double similarity = entry.getValue();
                    
                    // 检查其他用户是否对该帖子有正向行为
                    List<UserBehaviorEntity> otherUserBehaviors = userBehaviorsMap.get(otherUserId);
                    if (otherUserBehaviors != null) {
                        for (UserBehaviorEntity behavior : otherUserBehaviors) {
                            if (behavior.getRecipeId().equals(postId)) {
                                String behaviorType = behavior.getBehaviorType();
                                if ("like".equals(behaviorType) || "collect".equals(behaviorType)) {
                                    double weight = behaviorWeights.getOrDefault(behaviorType, 0.0);
                                    score += similarity * weight;
                                }
                                break;
                            }
                        }
                    }
                }
                
                scores.put(postId, score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return scores;
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
     * 计算健身目标匹配分数
     */
    private double calculateFitnessGoalScore(String userGoal, String postGoal) {
        if (userGoal == null || postGoal == null) {
            return 0.0;
        }
        
        if (userGoal.equals(postGoal)) {
            return 100.0; // 目标完全一致：100分
        } else if ("维持".equals(userGoal)) {
            return 90.0; // 维持用户对所有目标都有较高分数
        } else if ("维持".equals(postGoal)) {
            return 70.0; // 其他用户对维持目标有中等分数
        } else {
            return 0.0; // 不一致：0分
        }
    }

    /**
     * 计算营养标签匹配分数
     */
    private double calculateNutritionTagScore(String userGoal, String nutritionTag) {
        // 帖子未填 nutrition_tag，默认按均衡处理
        if (nutritionTag == null || nutritionTag.isEmpty()) {
            nutritionTag = "均衡";
        }
        
        // 如果用户健身目标为null，返回0.0
        if (userGoal == null) {
            return 0.0;
        }
        
        switch (userGoal) {
            case "增肌":
                if (nutritionTag.contains("高蛋白")) {
                    return 100.0;
                } else if (nutritionTag.contains("高脂")) {
                    return 80.0;  // 增肌用户需要适量脂肪支持激素分泌
                } else if (nutritionTag.contains("均衡")) {
                    return 50.0;
                } else {
                    return 0.0;
                }
            case "减脂":
                if (nutritionTag.contains("低卡")) {
                    return 100.0;
                } else if (nutritionTag.contains("均衡")) {
                    return 50.0;
                } else if (nutritionTag.contains("高脂")) {
                    return 20.0;  // 减脂用户应控制脂肪摄入
                } else {
                    return 0.0;
                }
            case "维持":
                if (nutritionTag.contains("均衡")) {
                    return 100.0;
                } else if (nutritionTag.contains("高蛋白") || nutritionTag.contains("低卡")) {
                    return 50.0;
                } else if (nutritionTag.contains("高脂")) {
                    return 40.0;  // 维持用户应适量控制脂肪摄入
                } else {
                    return 50.0;
                }
            default:
                return 50.0;
        }
    }

    /**
     * 归一化分数到 0~100
     */
    private Map<Long, Double> normalizeScores(Map<Long, Double> scores) {
        if (scores.isEmpty()) {
            return scores;
        }
        
        double maxScore = Collections.max(scores.values());
        if (maxScore == 0) {
            return scores;
        }
        
        Map<Long, Double> normalizedScores = new HashMap<>();
        for (Map.Entry<Long, Double> entry : scores.entrySet()) {
            double normalizedScore = (entry.getValue() / maxScore) * 100;
            normalizedScores.put(entry.getKey(), normalizedScore);
        }
        
        return normalizedScores;
    }

    /**
     * 保存推荐结果（Top 15）
     */
    private void saveRecommendations(Long userId, Map<Long, RecommendationScoreDetail> scores) {
        try {
            // 先删除用户的旧推荐
            jdbcTemplate.update("DELETE FROM recipe_recommendation WHERE user_id = ?", userId);
            
            // 提取总分并归一化
            Map<Long, Double> totalScores = new HashMap<>();
            for (Map.Entry<Long, RecommendationScoreDetail> entry : scores.entrySet()) {
                totalScores.put(entry.getKey(), entry.getValue().getTotalScore());
            }
            Map<Long, Double> normalizedScores = normalizeScores(totalScores);
            
            // 按归一化后的分数排序
            List<Map.Entry<Long, Double>> sortedEntries = new ArrayList<>(normalizedScores.entrySet());
            sortedEntries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
            
            // 取Top 15插入
            int count = 0;
            for (Map.Entry<Long, Double> entry : sortedEntries) {
                if (count >= 15) {
                    break;
                }
                
                // 插入推荐记录（保存归一化后的总分）
                jdbcTemplate.update(
                    "INSERT INTO recipe_recommendation (user_id, recipe_id, score, created_at) VALUES (?, ?, ?, NOW())",
                    userId, entry.getKey(), entry.getValue()
                );
                
                count++;
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
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> recommendations = new ArrayList<>();
        int total = 0;
        
        // 获取用户资料
        YonghuEntity userProfile = null;
        try {
            userProfile = yonghuService.selectById(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 如果用户资料为null，创建默认的用户资料
        if (userProfile == null) {
            userProfile = new YonghuEntity();
            userProfile.setId(userId);
            userProfile.setFitnessGoal("维持"); // 默认健身目标为维持
            userProfile.setDietaryRestrictions(""); // 默认无饮食禁忌
        }
        
        List<Map<String, Object>> postWithScores = new ArrayList<>();
        try {
            // 查询推荐的帖子ID和分数，按分数倒序，限制数量
            postWithScores = jdbcTemplate.queryForList(
                "SELECT recipe_id, score FROM recipe_recommendation WHERE user_id = ? ORDER BY score DESC LIMIT ?",
                userId, limit
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (postWithScores.isEmpty()) {
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
                // 重新查询推荐的帖子ID和分数，按分数倒序，限制数量
                postWithScores = jdbcTemplate.queryForList(
                    "SELECT recipe_id, score FROM recipe_recommendation WHERE user_id = ? ORDER BY score DESC LIMIT ?",
                    userId, limit
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 查询总帖子数
            try {
                total = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM recipe_recommendation WHERE user_id = ?",
                    Integer.class, userId
                );
            } catch (Exception e) {
                e.printStackTrace();
                total = 0;
            }
        }
        
        if (!postWithScores.isEmpty()) {
            // 提取帖子ID
            List<Long> postIds = postWithScores.stream()
                .map(post -> ((Number) post.get("recipe_id")).longValue())
                .collect(Collectors.toList());
            
            // 查询帖子详情
            List<RecipeEntity> recipeEntities = recipeDao.selectBatchIds(postIds);
            
            // 创建帖子ID到帖子详情的映射
            Map<Long, RecipeEntity> recipeMap = new HashMap<>();
            if (recipeEntities != null) {
                for (RecipeEntity recipe : recipeEntities) {
                    if (recipe != null) {
                        recipeMap.put(recipe.getId(), recipe);
                    }
                }
            }
            
            // 获取用户行为数据
            List<UserBehaviorEntity> userBehaviors = userBehaviorDao.getUserBehaviors(userId);
            
            // 如果用户没有行为数据，添加一些默认的行为数据用于测试
            if (userBehaviors == null || userBehaviors.isEmpty()) {
                userBehaviors = new ArrayList<>();
                // 添加一些默认的行为数据
                UserBehaviorEntity behavior1 = new UserBehaviorEntity();
                behavior1.setUserId(userId);
                behavior1.setRecipeId(1L);
                behavior1.setBehaviorType("like");
                userBehaviors.add(behavior1);
                UserBehaviorEntity behavior2 = new UserBehaviorEntity();
                behavior2.setUserId(userId);
                behavior2.setRecipeId(2L);
                behavior2.setBehaviorType("collect");
                userBehaviors.add(behavior2);
            }
            
            Map<Long, Double> userPreferences = new HashMap<>();
            if (userBehaviors != null && !userBehaviors.isEmpty()) {
                userPreferences = calculateUserPreferences(userBehaviors);
            }
            
            // 为每个帖子添加分数和详细得分数据
            for (Map<String, Object> postWithScore : postWithScores) {
                Long postId = ((Number) postWithScore.get("recipe_id")).longValue();
                Double score = ((Number) postWithScore.get("score")).doubleValue();
                RecipeEntity recipe = recipeMap.get(postId);
                
                if (recipe != null) {
                    // 跳过用户自己发布的帖子
                    if (recipe.getUserId() != null && recipe.getUserId().equals(userId)) {
                        continue;
                    }
                    
                    // 创建包含帖子详情和分数的Map
                    Map<String, Object> recommendation = new HashMap<>();
                    recommendation.put("id", recipe.getId());
                    recommendation.put("title", recipe.getTitle());
                    recommendation.put("description", recipe.getDescription());
                    // 使用数据库中存储的封面图片路径
                    recommendation.put("cover_image", recipe.getCoverImage());
                    recommendation.put("fitnessGoal", recipe.getFitnessGoal());
                    recommendation.put("dietaryRestrictions", recipe.getDietaryRestrictions());
                    recommendation.put("nutritionTag", recipe.getNutritionTag());
                    recommendation.put("mealScene", recipe.getMealScene());
                    recommendation.put("totalCalories", recipe.getTotalCalories());
                    recommendation.put("userId", recipe.getUserId());
                    recommendation.put("username", recipe.getUsername());
                    recommendation.put("status", recipe.getStatus());
                    recommendation.put("addtime", recipe.getAddtime());
                    recommendation.put("score", score);
                    
                    // 计算详细的得分数据
                    if (userProfile != null) {
                        // 获取用户的AI问题
                        List<Map<String, Object>> aiQuestions = getUserAIQuestions(userId);
                        // 提取AI问题中的健身目标关键词
                        Map<String, Integer> keywordCount = extractFitnessGoalKeywords(aiQuestions);
                        
                        if (userBehaviors != null && !userBehaviors.isEmpty()) {
                            // 有行为用户
                            Map<Long, Double> collaborativeScores = calculateCollaborativeScores(userId, recipeEntities, userPreferences);
                            Map<Long, Double> normalizedCollaborativeScores = normalizeScores(collaborativeScores);
                            double collaborativeScore = normalizedCollaborativeScores.getOrDefault(postId, 0.0);
                            double fitnessGoalScore = calculateFitnessGoalScore(userProfile.getFitnessGoal(), recipe.getFitnessGoal());
                            double nutritionTagScore = calculateNutritionTagScore(userProfile.getFitnessGoal(), recipe.getNutritionTag());
                            double aiQuestionScore = calculateAIQuestionScore(userProfile.getFitnessGoal(), keywordCount, recipe.getFitnessGoal());
                            
                            // 添加详细得分数据
                            recommendation.put("collaborativeScore", collaborativeScore);
                            recommendation.put("fitnessGoalScore", fitnessGoalScore);
                            recommendation.put("nutritionTagScore", nutritionTagScore);
                            recommendation.put("aiQuestionScore", aiQuestionScore);
                        } else {
                            // 冷启动用户
                            double fitnessGoalScore = calculateFitnessGoalScore(userProfile.getFitnessGoal(), recipe.getFitnessGoal());
                            double nutritionTagScore = calculateNutritionTagScore(userProfile.getFitnessGoal(), recipe.getNutritionTag());
                            double aiQuestionScore = calculateAIQuestionScore(userProfile.getFitnessGoal(), keywordCount, recipe.getFitnessGoal());
                            
                            // 添加详细得分数据
                            recommendation.put("collaborativeScore", 0.0);
                            recommendation.put("fitnessGoalScore", fitnessGoalScore);
                            recommendation.put("nutritionTagScore", nutritionTagScore);
                            recommendation.put("aiQuestionScore", aiQuestionScore);
                        }
                    } else {
                        // 用户档案为null，所有分数设为0
                        recommendation.put("collaborativeScore", 0.0);
                        recommendation.put("fitnessGoalScore", 0.0);
                        recommendation.put("nutritionTagScore", 0.0);
                        recommendation.put("aiQuestionScore", 0.0);
                    }
                    
                    recommendations.add(recommendation);
                }
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
     * 获取推荐分数
     */
    public Double getRecommendationScore(Long userId, Long postId) {
        try {
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
     * 获取所有帖子（按发布时间倒序）
     */
    public Map<String, Object> getAllPosts(int limit, int page) {
        // 计算偏移量
        int offset = (page - 1) * limit;
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> posts = new ArrayList<>();
        int total = 0;
        
        try {
            // 查询总帖子数
            total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM recipe WHERE status = 'approved'",
                Integer.class
            );
        } catch (Exception e) {
            e.printStackTrace();
            total = 0;
        }
        
        try {
            // 查询帖子列表，按发布时间倒序
            List<Long> postIds = jdbcTemplate.queryForList(
                "SELECT id FROM recipe WHERE status = 'approved' ORDER BY addtime DESC LIMIT ? OFFSET ?",
                Long.class, limit, offset
            );
            
            if (!postIds.isEmpty()) {
                List<RecipeEntity> recipeEntities = recipeDao.selectBatchIds(postIds);
                
                // 为每个帖子添加营养标签（如果为null则设置为"均衡"）
                for (RecipeEntity recipe : recipeEntities) {
                    Map<String, Object> post = new HashMap<>();
                    post.put("id", recipe.getId());
                    post.put("title", recipe.getTitle());
                    post.put("description", recipe.getDescription());
                    // 使用数据库中存储的封面图片路径
                    post.put("cover_image", recipe.getCoverImage());
                    post.put("fitnessGoal", recipe.getFitnessGoal());
                    post.put("dietaryRestrictions", recipe.getDietaryRestrictions());
                    post.put("nutritionTag", recipe.getNutritionTag() != null ? recipe.getNutritionTag() : "均衡");
                    post.put("mealScene", recipe.getMealScene());
                    post.put("totalCalories", recipe.getTotalCalories());
                    post.put("userId", recipe.getUserId());
                    post.put("username", recipe.getUsername());
                    post.put("status", recipe.getStatus());
                    post.put("addtime", recipe.getAddtime());
                    
                    posts.add(post);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        result.put("data", posts);
        result.put("total", total);
        return result;
    }

    /**
     * 搜索帖子
     */
    public Map<String, Object> searchPosts(String keyword, int limit, int page) {
        // 计算偏移量
        int offset = (page - 1) * limit;
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> posts = new ArrayList<>();
        int total = 0;
        
        try {
            // 查询总帖子数
            total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM recipe WHERE status = 'approved' AND (title LIKE CONCAT('%', ?, '%') OR description LIKE CONCAT('%', ?, '%'))",
                Integer.class, keyword, keyword
            );
        } catch (Exception e) {
            e.printStackTrace();
            total = 0;
        }
        
        try {
            // 查询帖子列表，按发布时间倒序
            List<Long> postIds = jdbcTemplate.queryForList(
                "SELECT id FROM recipe WHERE status = 'approved' AND (title LIKE CONCAT('%', ?, '%') OR description LIKE CONCAT('%', ?, '%')) ORDER BY addtime DESC LIMIT ? OFFSET ?",
                Long.class, keyword, keyword, limit, offset
            );
            
            if (!postIds.isEmpty()) {
                List<RecipeEntity> recipeEntities = recipeDao.selectBatchIds(postIds);
                
                // 为每个帖子添加营养标签（如果为null则设置为"均衡"）
                for (RecipeEntity recipe : recipeEntities) {
                    Map<String, Object> post = new HashMap<>();
                    post.put("id", recipe.getId());
                    post.put("title", recipe.getTitle());
                    post.put("description", recipe.getDescription());
                    // 使用数据库中存储的封面图片路径
                    post.put("cover_image", recipe.getCoverImage());
                    post.put("fitnessGoal", recipe.getFitnessGoal());
                    post.put("dietaryRestrictions", recipe.getDietaryRestrictions());
                    post.put("nutritionTag", recipe.getNutritionTag() != null ? recipe.getNutritionTag() : "均衡");
                    post.put("mealScene", recipe.getMealScene());
                    post.put("totalCalories", recipe.getTotalCalories());
                    post.put("userId", recipe.getUserId());
                    post.put("username", recipe.getUsername());
                    post.put("status", recipe.getStatus());
                    post.put("addtime", recipe.getAddtime());
                    
                    posts.add(post);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        result.put("data", posts);
        result.put("total", total);
        return result;
    }
}