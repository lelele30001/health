package com.cl.controller;

import com.cl.entity.RecipeEntity;
import com.cl.service.PostRecommendationService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 帖子推荐控制器
 * 提供个性化帖子推荐相关的API接口
 */
@RestController
@RequestMapping("/postRecommendation")
public class PostRecommendationController {

    @Autowired
    private PostRecommendationService postRecommendationService;

    /**
     * 获取用户个性化推荐帖子列表
     */
    @GetMapping("/list")
    public R getRecommendations(@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            // 未登录用户返回热门帖子
            List<RecipeEntity> hotPosts = postRecommendationService.getHotPosts(limit);
            return R.ok().put("data", hotPosts).put("type", "hot").put("total", hotPosts.size());
        }
        
        Map<String, Object> result = postRecommendationService.getRecommendationsWithTotal(userId, limit, page);
        return R.ok().put("data", result.get("data")).put("type", "personalized").put("total", result.get("total"));
    }

    /**
     * 手动生成用户推荐
     */
    @PostMapping("/generate")
    public R generateRecommendations(HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        postRecommendationService.generateRecommendations(userId);
        return R.ok("推荐生成成功");
    }

    /**
     * 获取帖子的推荐分数
     */
    @GetMapping("/score")
    public R getRecommendationScore(@RequestParam Long postId, HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        Double score = postRecommendationService.getRecommendationScore(userId, postId);
        return R.ok().put("score", score);
    }

    /**
     * 获取热门帖子（用于新用户冷启动）
     */
    @GetMapping("/hot")
    public R getHotPosts(@RequestParam(defaultValue = "10") int limit) {
        List<RecipeEntity> hotPosts = postRecommendationService.getHotPosts(limit);
        return R.ok().put("data", hotPosts);
    }

    /**
     * 刷新用户推荐
     */
    @PostMapping("/refresh")
    public R refreshRecommendations(@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            // 未登录用户返回热门帖子
            List<RecipeEntity> hotPosts = postRecommendationService.getHotPosts(limit);
            return R.ok().put("data", hotPosts).put("type", "hot").put("total", hotPosts.size());
        }
        
        // 重新生成推荐
        postRecommendationService.generateRecommendations(userId);
        Map<String, Object> result = postRecommendationService.getRecommendationsWithTotal(userId, limit, page);
        return R.ok().put("data", result.get("data")).put("type", "personalized").put("total", result.get("total"));
    }
}
