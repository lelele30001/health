package com.cl.controller;

import com.cl.entity.UserBehaviorEntity;
import com.cl.service.UserBehaviorService;
import com.cl.service.RecommendationService;
import com.cl.service.UsersService;
import com.cl.service.YonghuService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user-behavior")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private YonghuService yonghuService;

    // 记录用户行为
    @PostMapping("/record")
    public R recordBehavior(@RequestBody UserBehaviorEntity behavior, HttpServletRequest request) {
        // 从请求体中获取用户ID
        Long userId = behavior.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 检查用户是否存在
        boolean userExists = usersService.selectById(userId) != null || yonghuService.selectById(userId) != null;
        if (!userExists) {
            return R.error("用户不存在");
        }
        
        boolean recorded = userBehaviorService.recordUserBehavior(behavior);
        
        if (recorded) {
            // 重新生成推荐
            recommendationService.generateRecommendations(userId);
            return R.ok("行为记录成功");
        } else {
            return R.error("行为记录失败");
        }
    }

    // 删除用户行为
    @DeleteMapping("/delete")
    public R deleteBehavior(@RequestParam Long recipeId, @RequestParam String behaviorType, @RequestParam Long userId) {
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 检查用户是否存在
        boolean userExists = usersService.selectById(userId) != null || yonghuService.selectById(userId) != null;
        if (!userExists) {
            return R.error("用户不存在");
        }
        
        boolean deleted = userBehaviorService.deleteUserBehavior(userId, recipeId, behaviorType);
        
        if (deleted) {
            // 重新生成推荐
            recommendationService.generateRecommendations(userId);
            return R.ok("行为删除成功");
        } else {
            return R.error("行为删除失败");
        }
    }

    // 获取用户行为
    @GetMapping("/get")
    public R getUserBehavior(@RequestParam Long recipeId, @RequestParam String behaviorType, @RequestParam Long userId) {
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 检查用户是否存在
        boolean userExists = usersService.selectById(userId) != null || yonghuService.selectById(userId) != null;
        if (!userExists) {
            return R.error("用户不存在");
        }
        
        UserBehaviorEntity behavior = userBehaviorService.getUserBehavior(userId, recipeId, behaviorType);
        return R.ok().put("behavior", behavior);
    }

    // 获取用户的所有行为
    @GetMapping("/user/all")
    public R getUserBehaviors(@RequestParam Long userId) {
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 检查用户是否存在
        boolean userExists = usersService.selectById(userId) != null || yonghuService.selectById(userId) != null;
        if (!userExists) {
            return R.error("用户不存在");
        }
        
        return R.ok().put("behaviors", userBehaviorService.getUserBehaviors(userId));
    }

    // 获取食谱的所有行为
    @GetMapping("/recipe/all")
    public R getRecipeBehaviors(@RequestParam Long recipeId) {
        return R.ok().put("behaviors", userBehaviorService.getRecipeBehaviors(recipeId));
    }

    // 统计食谱行为数量
    @GetMapping("/recipe/count")
    public R countRecipeBehaviors(@RequestParam Long recipeId) {
        int likeCount = userBehaviorService.countRecipeBehaviorByType(recipeId, "like");
        int collectCount = userBehaviorService.countRecipeBehaviorByType(recipeId, "collect");
        int commentCount = userBehaviorService.countRecipeBehaviorByType(recipeId, "comment");
        int dislikeCount = userBehaviorService.countRecipeBehaviorByType(recipeId, "dislike");
        
        return R.ok()
                .put("likeCount", likeCount)
                .put("collectCount", collectCount)
                .put("commentCount", commentCount)
                .put("dislikeCount", dislikeCount);
    }
}