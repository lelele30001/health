package com.cl.controller;

import com.cl.entity.UserPostBehaviorEntity;
import com.cl.service.UserPostBehaviorService;
import com.cl.service.PostRecommendationService;
import com.cl.service.UsersService;
import com.cl.service.YonghuService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user-post-behavior")
public class UserPostBehaviorController {

    @Autowired
    private UserPostBehaviorService userPostBehaviorService;

    @Autowired
    private PostRecommendationService postRecommendationService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private YonghuService yonghuService;

    // 记录用户帖子行为
    @PostMapping("/record")
    public R recordBehavior(@RequestBody UserPostBehaviorEntity behavior, HttpServletRequest request) {
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
        
        boolean recorded = userPostBehaviorService.recordUserBehavior(behavior);
        
        if (recorded) {
            // 重新生成推荐
            postRecommendationService.generateRecommendations(userId);
            return R.ok("行为记录成功");
        } else {
            return R.error("行为记录失败");
        }
    }

    // 删除用户帖子行为
    @DeleteMapping("/delete")
    public R deleteBehavior(@RequestParam Long postId, @RequestParam String behaviorType, @RequestParam Long userId) {
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 检查用户是否存在
        boolean userExists = usersService.selectById(userId) != null || yonghuService.selectById(userId) != null;
        if (!userExists) {
            return R.error("用户不存在");
        }
        
        boolean deleted = userPostBehaviorService.deleteUserBehavior(userId, postId, behaviorType);
        
        if (deleted) {
            // 重新生成推荐
            postRecommendationService.generateRecommendations(userId);
            return R.ok("行为删除成功");
        } else {
            return R.error("行为删除失败");
        }
    }

    // 获取用户帖子行为
    @GetMapping("/get")
    public R getUserBehavior(@RequestParam Long postId, @RequestParam String behaviorType, @RequestParam Long userId) {
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 检查用户是否存在
        boolean userExists = usersService.selectById(userId) != null || yonghuService.selectById(userId) != null;
        if (!userExists) {
            return R.error("用户不存在");
        }
        
        UserPostBehaviorEntity behavior = userPostBehaviorService.getUserBehavior(userId, postId, behaviorType);
        return R.ok().put("behavior", behavior);
    }

    // 获取用户的所有帖子行为
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
        
        return R.ok().put("behaviors", userPostBehaviorService.getUserBehaviors(userId));
    }

    // 获取帖子的所有行为
    @GetMapping("/post/all")
    public R getPostBehaviors(@RequestParam Long postId) {
        return R.ok().put("behaviors", userPostBehaviorService.getPostBehaviors(postId));
    }

    // 统计帖子行为数量
    @GetMapping("/post/count")
    public R countPostBehaviors(@RequestParam Long postId) {
        int likeCount = userPostBehaviorService.countPostBehaviorByType(postId, "like");
        int collectCount = userPostBehaviorService.countPostBehaviorByType(postId, "collect");
        int dislikeCount = userPostBehaviorService.countPostBehaviorByType(postId, "dislike");
        
        return R.ok()
                .put("likeCount", likeCount)
                .put("collectCount", collectCount)
                .put("dislikeCount", dislikeCount);
    }

    // 同步所有收藏行为到 storeup 表
    @PostMapping("/sync-collect")
    public R syncCollectBehaviors() {
        userPostBehaviorService.syncAllCollectBehaviors();
        return R.ok("收藏行为同步成功");
    }
}
