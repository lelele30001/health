package com.cl.controller;

import com.cl.entity.RecipeEntity;
import com.cl.service.PostRecommendationService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    public R getRecommendations(@RequestParam(defaultValue = "15") int limit, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            // 未登录用户返回空数据
            return R.ok().put("data", new ArrayList<>()).put("type", "hot").put("total", 0);
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
     * 获取所有帖子（按发布时间倒序）
     */
    @GetMapping("/all")
    public R getAllPosts(@RequestParam(defaultValue = "15") int limit, @RequestParam(defaultValue = "1") int page) {
        Map<String, Object> result = postRecommendationService.getAllPosts(limit, page);
        return R.ok().put("data", result.get("data")).put("total", result.get("total"));
    }

    /**
     * 搜索帖子
     */
    @GetMapping("/search")
    public R searchPosts(@RequestParam String keyword, @RequestParam(defaultValue = "15") int limit, @RequestParam(defaultValue = "1") int page) {
        Map<String, Object> result = postRecommendationService.searchPosts(keyword, limit, page);
        return R.ok().put("data", result.get("data")).put("total", result.get("total"));
    }

    /**
     * 刷新用户推荐
     */
    @PostMapping("/refresh")
    public R refreshRecommendations(@RequestParam(defaultValue = "15") int limit, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            // 未登录用户返回空数据
            return R.ok().put("data", new ArrayList<>()).put("type", "hot").put("total", 0);
        }
        
        // 重新生成推荐
        postRecommendationService.generateRecommendations(userId);
        Map<String, Object> result = postRecommendationService.getRecommendationsWithTotal(userId, limit, page);
        return R.ok().put("data", result.get("data")).put("type", "personalized").put("total", result.get("total"));
    }
}
