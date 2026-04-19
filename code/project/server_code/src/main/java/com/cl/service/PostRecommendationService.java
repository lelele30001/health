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
    private double totalScore; // 总分
    private double popularityScore; // 热度分数
    private Long addtime; // 发布时间

    public RecommendationScoreDetail(double collaborativeScore, double fitnessGoalScore, double nutritionTagScore, double totalScore) {
        this.collaborativeScore = collaborativeScore;
        this.fitnessGoalScore = fitnessGoalScore;
        this.nutritionTagScore = nutritionTagScore;
        this.totalScore = totalScore;
        this.popularityScore = 0.0;
        this.addtime = 0L;
    }

    public RecommendationScoreDetail(double collaborativeScore, double fitnessGoalScore, double nutritionTagScore, double totalScore, double popularityScore, Long addtime) {
        this.collaborativeScore = collaborativeScore;
        this.fitnessGoalScore = fitnessGoalScore;
        this.nutritionTagScore = nutritionTagScore;
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

    public PostRecommendationService() {
        // 初始化行为权重
        behaviorWeights.put("collect", 10.0);   // 收藏：10分
        behaviorWeights.put("like", 5.0);       // 点赞：5分
        behaviorWeights.put("view", 1.0);       // 浏览：1分
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
        } else {
            // 使用用户档案中的健身目标
            System.out.println("使用用户档案中的健身目标: " + userProfile.getFitnessGoal());
        }
        
        // 2. 获取用户帖子行为数据
        List<UserBehaviorEntity> userBehaviors = userBehaviorDao.getUserBehaviors(userId);
        
        // 如果用户没有行为数据，添加一些默认的行为数据用于测试
        if (userBehaviors == null || userBehaviors.isEmpty()) {
            System.out.println("=== 为用户" + userId + "添加默认行为数据 ===");
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
            System.out.println("已添加" + userBehaviors.size() + "条默认行为数据");
        }
        
        // 3. 获取所有已审核通过的帖子并过滤饮食禁忌
        List<RecipeEntity> allPosts = recipeDao.selectList(
            new EntityWrapper<RecipeEntity>()
                .eq("status", "approved")
        );
        System.out.println("=== 帖子数据 ===");
        System.out.println("从数据库获取的帖子数量: " + (allPosts != null ? allPosts.size() : 0));
        if (allPosts != null) {
            for (RecipeEntity post : allPosts) {
                System.out.println("帖子ID: " + post.getId() + "，标题: " + post.getTitle() + "，用户ID: " + post.getUserId());
            }
        }
        
        // 过滤用户自己发布的帖子
        List<RecipeEntity> filteredPostsByUser = new ArrayList<>();
        for (RecipeEntity post : allPosts) {
            if (post.getUserId() == null || !post.getUserId().equals(userId)) {
                filteredPostsByUser.add(post);
            } else {
                System.out.println("过滤掉用户自己发布的帖子: " + post.getId() + " (" + post.getTitle() + ")");
            }
        }
        
        System.out.println("过滤用户后帖子数量: " + filteredPostsByUser.size());
        
        List<RecipeEntity> filteredPosts = filterPostsByDietaryRestrictions(filteredPostsByUser, userProfile.getDietaryRestrictions());
        System.out.println("过滤饮食禁忌后帖子数量: " + filteredPosts.size());
        System.out.println("用户饮食禁忌: " + (userProfile.getDietaryRestrictions() != null ? userProfile.getDietaryRestrictions() : "无"));
        
        // 4. 计算推荐分数
        Map<Long, RecommendationScoreDetail> recommendationScores = new HashMap<>();
        if (userBehaviors != null && !userBehaviors.isEmpty()) {
            // 有行为用户：协同相似度 40% + 健身目标匹配 30% + 营养标签匹配 20%
            Map<Long, Double> userPreferences = calculateUserPreferences(userBehaviors);
            recommendationScores = calculateRecommendationScores(userId, userProfile, filteredPosts, userPreferences);
        } else {
            // 无行为冷启动：健身目标匹配 60% + 营养标签匹配 40%
            recommendationScores = calculateColdStartScores(userProfile, filteredPosts);
        }
        
        // 5. 保存推荐（Top 15）
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
        System.out.println("=== 饮食禁忌过滤 ===");
        System.out.println("输入帖子数量: " + (posts != null ? posts.size() : 0));
        System.out.println("用户饮食禁忌: " + (forbiddenFoods != null ? forbiddenFoods : "null"));
        
        // 如果用户没有饮食禁忌，直接返回所有帖子
        if (forbiddenFoods == null || forbiddenFoods.trim().isEmpty() || "无".equals(forbiddenFoods.trim())) {
            System.out.println("无饮食禁忌，直接返回所有帖子");
            return posts;
        }
        
        // 有饮食禁忌，进行过滤
        String[] restrictions = forbiddenFoods.split(",");
        System.out.println("饮食禁忌列表: " + Arrays.toString(restrictions));
        
        List<RecipeEntity> filteredPosts = new ArrayList<>();
        for (RecipeEntity post : posts) {
            String tabooTags = post.getDietaryRestrictions();
            System.out.println("  帖子ID: " + post.getId() + "，标题: " + post.getTitle() + "，禁忌标签: " + (tabooTags != null ? tabooTags : "null"));
            
            // 如果帖子没有禁忌标签，通过
            if (tabooTags == null || tabooTags.trim().isEmpty()) {
                System.out.println("    帖子无禁忌标签，通过");
                filteredPosts.add(post);
                continue;
            }
            
            // 检查帖子是否包含用户的禁忌食物
            boolean hasRestriction = false;
            for (String restriction : restrictions) {
                String trimmedRestriction = restriction.trim();
                if (!trimmedRestriction.isEmpty() && tabooTags.contains(trimmedRestriction)) {
                    System.out.println("    帖子包含禁忌: " + trimmedRestriction + "，过滤掉");
                    hasRestriction = true;
                    break;
                }
            }
            
            if (!hasRestriction) {
                System.out.println("    帖子不包含禁忌，通过");
                filteredPosts.add(post);
            }
        }
        
        System.out.println("过滤后帖子数量: " + filteredPosts.size());
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
    private Map<Long, RecommendationScoreDetail> calculateRecommendationScores(Long userId, YonghuEntity userProfile, List<RecipeEntity> posts, Map<Long, Double> userPreferences) {
        Map<Long, RecommendationScoreDetail> scores = new HashMap<>();

        // 打印帖子数量
        System.out.println("=== 帖子数量 ===");
        System.out.println("总帖子数量: " + (posts != null ? posts.size() : 0));

        // 计算协同相似度
        Map<Long, Double> collaborativeScores = calculateCollaborativeScores(userId, posts, userPreferences);

        // 归一化协同相似度分数
        Map<Long, Double> normalizedCollaborativeScores = normalizeScores(collaborativeScores);

        // 计算帖子热度分数
        Map<Long, Double> popularityScores = calculatePostPopularity(posts);

        // 遍历所有帖子
        for (RecipeEntity post : posts) {
            Long postId = post.getId();

            // 跳过用户已经有行为的帖子
            if (userPreferences.containsKey(postId)) {
                System.out.println("跳过用户有行为的帖子: " + postId + " (" + post.getTitle() + ")");
                continue;
            }

            // 跳过用户自己发布的帖子
            if (post.getUserId() != null && post.getUserId().equals(userId)) {
                System.out.println("跳过用户自己发布的帖子: " + postId + " (" + post.getTitle() + ")");
                continue;
            }

            // 计算健身目标匹配分数
            double fitnessGoalScore = calculateFitnessGoalScore(userProfile.getFitnessGoal(), post.getFitnessGoal());

            // 计算营养标签匹配分数
            double nutritionTagScore = calculateNutritionTagScore(userProfile.getFitnessGoal(), post.getNutritionTag());

            // 加权计算总分
            double collaborativeScore = normalizedCollaborativeScores.getOrDefault(postId, 0.0);
            double totalScore = collaborativeScore * 0.5 + fitnessGoalScore * 0.3 + nutritionTagScore * 0.2;

            // 获取热度分数
            double popularityScore = popularityScores.getOrDefault(postId, 0.0);

            // 获取发布时间
            Long addtime = post.getAddtime() != null ? post.getAddtime().getTime() : 0L;

            // 保存详细的得分数据
            scores.put(postId, new RecommendationScoreDetail(collaborativeScore, fitnessGoalScore, nutritionTagScore, totalScore, popularityScore, addtime));
            System.out.println("添加推荐帖子: " + postId + " (" + post.getTitle() + ")，总得分: " + totalScore);
        }

        // 打印过滤后的帖子数量
        System.out.println("=== 过滤后结果 ===");
        System.out.println("推荐分数数量: " + scores.size());

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

        // 打印排序后的推荐顺序和得分
        System.out.println("=== 推荐排序结果 ===");
        int rank = 1;
        for (Map.Entry<Long, RecommendationScoreDetail> entry : sortedScores.entrySet()) {
            Long postId = entry.getKey();
            RecommendationScoreDetail scoreDetail = entry.getValue();
            System.out.println("排名 " + rank + ": 帖子ID " + postId + "，总得分: " + scoreDetail.getTotalScore() +
                    " (协同相似度: " + scoreDetail.getCollaborativeScore() +
                    ", 健身目标匹配: " + scoreDetail.getFitnessGoalScore() +
                    ", 营养标签匹配: " + scoreDetail.getNutritionTagScore() +
                    ", 热度: " + scoreDetail.getPopularityScore() +
                    ", 发布时间: " + scoreDetail.getAddtime() + ")");
            rank++;
        }

        return sortedScores;
    }

    /**
     * 计算冷启动推荐分数
     */
    private Map<Long, RecommendationScoreDetail> calculateColdStartScores(YonghuEntity userProfile, List<RecipeEntity> posts) {
        Map<Long, RecommendationScoreDetail> scores = new HashMap<>();

        // 计算帖子热度分数
        Map<Long, Double> popularityScores = calculatePostPopularity(posts);

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

            // 加权计算总分（冷启动用户权重调整为50%:50%）
            double totalScore = fitnessGoalScore * 0.5 + nutritionTagScore * 0.5;

            // 获取热度分数
            double popularityScore = popularityScores.getOrDefault(post.getId(), 0.0);

            // 获取发布时间
            Long addtime = post.getAddtime() != null ? post.getAddtime().getTime() : 0L;

            // 保存详细的得分数据（冷启动没有协同相似度分数，设为0）
            scores.put(post.getId(), new RecommendationScoreDetail(0.0, fitnessGoalScore, nutritionTagScore, totalScore, popularityScore, addtime));
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

        // 打印排序后的推荐顺序和得分
        System.out.println("=== 推荐排序结果 ===");
        int rank = 1;
        for (Map.Entry<Long, RecommendationScoreDetail> entry : sortedScores.entrySet()) {
            Long postId = entry.getKey();
            RecommendationScoreDetail scoreDetail = entry.getValue();
            System.out.println("排名 " + rank + ": 帖子ID " + postId + "，总得分: " + scoreDetail.getTotalScore() +
                    " (协同相似度: " + scoreDetail.getCollaborativeScore() +
                    ", 健身目标匹配: " + scoreDetail.getFitnessGoalScore() +
                    ", 营养标签匹配: " + scoreDetail.getNutritionTagScore() +
                    ", 热度: " + scoreDetail.getPopularityScore() +
                    ", 发布时间: " + scoreDetail.getAddtime() + ")");
            rank++;
        }

        return sortedScores;
    }

    /**
     * 计算协同相似度分数
     */
    private Map<Long, Double> calculateCollaborativeScores(Long userId, List<RecipeEntity> posts, Map<Long, Double> userPreferences) {
        Map<Long, Double> scores = new HashMap<>();
        
        try {
            // 使用实际的用户行为数据
            System.out.println("=== 使用实际用户行为数据 ===");
            
            // 获取所有用户的行为数据
            List<UserBehaviorEntity> allUserBehaviors = userBehaviorDao.selectList(null);
            System.out.println("总用户行为数据数量: " + allUserBehaviors.size());
            
            // 按用户分组
            Map<Long, List<UserBehaviorEntity>> userBehaviorsMap = new HashMap<>();
            for (UserBehaviorEntity behavior : allUserBehaviors) {
                Long otherUserId = behavior.getUserId();
                if (!userBehaviorsMap.containsKey(otherUserId)) {
                    userBehaviorsMap.put(otherUserId, new ArrayList<>());
                }
                userBehaviorsMap.get(otherUserId).add(behavior);
            }
            
            System.out.println("有行为的用户数量: " + userBehaviorsMap.size());
            
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
                    System.out.println("用户" + userId + "与用户" + otherUserId + "的相似度: " + similarity);
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
                                    System.out.println("用户" + otherUserId + "对帖子" + postId + "的" + behaviorType + "行为，权重: " + weight + "，贡献分数: " + (similarity * weight));
                                }
                                break;
                            }
                        }
                    }
                }
                
                scores.put(postId, score);
                System.out.println("帖子" + postId + "的协同过滤分数: " + score);
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
        } else {
            // 使用用户档案中的健身目标
            System.out.println("使用用户档案中的健身目标: " + userProfile.getFitnessGoal());
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
                System.out.println("=== 为用户" + userId + "添加默认行为数据 ===");
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
                System.out.println("已添加" + userBehaviors.size() + "条默认行为数据");
            }
            
            Map<Long, Double> userPreferences = new HashMap<>();
            if (userBehaviors != null && !userBehaviors.isEmpty()) {
                userPreferences = calculateUserPreferences(userBehaviors);
                System.out.println("用户偏好数据: " + userPreferences);
            }
            
            // 为每个帖子添加分数和详细得分数据
            for (Map<String, Object> postWithScore : postWithScores) {
                Long postId = ((Number) postWithScore.get("recipe_id")).longValue();
                Double score = ((Number) postWithScore.get("score")).doubleValue();
                RecipeEntity recipe = recipeMap.get(postId);
                
                if (recipe != null) {
                    // 打印调试信息，确认帖子的userId和当前用户的userId
                    System.out.println("=== 帖子信息 ===");
                    System.out.println("帖子ID: " + postId);
                    System.out.println("帖子标题: " + recipe.getTitle());
                    System.out.println("帖子userId: " + recipe.getUserId());
                    System.out.println("当前用户userId: " + userId);
                    System.out.println("是否是当前用户发布的帖子: " + (recipe.getUserId() != null && recipe.getUserId().equals(userId)));
                    
                    // 跳过用户自己发布的帖子
                    if (recipe.getUserId() != null && recipe.getUserId().equals(userId)) {
                        System.out.println("跳过用户自己发布的帖子: " + postId);
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
                    
                    // 打印调试信息
                    System.out.println("=== 调试信息 ===");
                    System.out.println("用户ID: " + userId);
                    System.out.println("用户健身目标: " + (userProfile != null ? userProfile.getFitnessGoal() : "null"));
                    System.out.println("帖子ID: " + postId);
                    System.out.println("帖子标题: " + recipe.getTitle());
                    System.out.println("帖子健身目标: " + recipe.getFitnessGoal());
                    System.out.println("帖子营养标签: " + recipe.getNutritionTag());
                    System.out.println("userBehaviors 大小: " + (userBehaviors != null ? userBehaviors.size() : 0));
                    
                    // 计算详细的得分数据
                    if (userProfile != null) {
                        if (userBehaviors != null && !userBehaviors.isEmpty()) {
                            // 有行为用户
                            System.out.println("=== 有行为用户 ===");
                            Map<Long, Double> collaborativeScores = calculateCollaborativeScores(userId, recipeEntities, userPreferences);
                            System.out.println("协同相似度分数: " + collaborativeScores.get(postId));
                            Map<Long, Double> normalizedCollaborativeScores = normalizeScores(collaborativeScores);
                            System.out.println("归一化协同相似度分数: " + normalizedCollaborativeScores.get(postId));
                            double collaborativeScore = normalizedCollaborativeScores.getOrDefault(postId, 0.0);
                            double fitnessGoalScore = calculateFitnessGoalScore(userProfile.getFitnessGoal(), recipe.getFitnessGoal());
                            System.out.println("健身目标匹配分数: " + fitnessGoalScore);
                            double nutritionTagScore = calculateNutritionTagScore(userProfile.getFitnessGoal(), recipe.getNutritionTag());
                            System.out.println("营养标签匹配分数: " + nutritionTagScore);
                            
                            // 添加详细得分数据
                            recommendation.put("collaborativeScore", collaborativeScore);
                            recommendation.put("fitnessGoalScore", fitnessGoalScore);
                            recommendation.put("nutritionTagScore", nutritionTagScore);
                        } else {
                            // 冷启动用户
                            System.out.println("=== 冷启动用户 ===");
                            double fitnessGoalScore = calculateFitnessGoalScore(userProfile.getFitnessGoal(), recipe.getFitnessGoal());
                            System.out.println("健身目标匹配分数: " + fitnessGoalScore);
                            double nutritionTagScore = calculateNutritionTagScore(userProfile.getFitnessGoal(), recipe.getNutritionTag());
                            System.out.println("营养标签匹配分数: " + nutritionTagScore);
                            
                            // 添加详细得分数据
                            recommendation.put("collaborativeScore", 0.0);
                            recommendation.put("fitnessGoalScore", fitnessGoalScore);
                            recommendation.put("nutritionTagScore", nutritionTagScore);
                        }
                    } else {
                        // 用户档案为null，所有分数设为0
                        System.out.println("=== 用户档案为null ===");
                        recommendation.put("collaborativeScore", 0.0);
                        recommendation.put("fitnessGoalScore", 0.0);
                        recommendation.put("nutritionTagScore", 0.0);
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