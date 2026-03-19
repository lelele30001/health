package com.cl.controller;

import com.cl.service.RecommendationService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    // 获取用户推荐列表
    @GetMapping("/get")
    public R getRecommendations(@RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        return R.ok().put("recipes", recommendationService.getRecommendations(userId, limit));
    }

    // 手动生成推荐
    @PostMapping("/generate")
    public R generateRecommendations(HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        recommendationService.generateRecommendations(userId);
        return R.ok("推荐生成成功");
    }

    // 获取推荐分数
    @GetMapping("/score")
    public R getRecommendationScore(@RequestParam Long recipeId, HttpServletRequest request) {
        // 从会话中获取用户ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        Double score = recommendationService.getRecommendationScore(userId, recipeId);
        return R.ok().put("score", score);
    }
}