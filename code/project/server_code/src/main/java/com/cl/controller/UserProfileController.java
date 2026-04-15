package com.cl.controller;

import com.cl.entity.YonghuEntity;
import com.cl.service.YonghuService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户档案控制器
 */
@RestController
// @RequestMapping({"/user/profile", "/user-profile"})
// 改成
// @RequestMapping({"/project/user/profile", "/project/user-profile"})
// 把你原来的全部替换成这一行！！！
@RequestMapping("/user-profile")
public class UserProfileController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    
    @Autowired
    private YonghuService yonghuService;

    /**
     * 获取用户档案
     */
    @GetMapping("/get")
    public R getProfile(@RequestParam Long userId) {
        YonghuEntity profile = yonghuService.selectById(userId);
        return R.ok().put("profile", profile);
    }

    /**
     * 保存/更新用户档案
     */
    @PostMapping("/save")
    public R saveProfile(@RequestBody YonghuEntity profile) {
        // 计算每日热量需求
        int dailyCalories = calculateDailyCalories(
            profile.getWeight(),
            profile.getHeight(),
            profile.getAge(),
            profile.getGender(),
            profile.getFitnessGoal()
        );
        profile.setDailyCalories(dailyCalories);
        
        boolean success = yonghuService.updateById(profile);
        if (success) {
            return R.ok().put("profile", profile);
        } else {
            return R.error("保存失败");
        }
    }

    /**
     * 获取用户目标数据
     */
    @GetMapping("/get-goals")
    public R getGoals(@RequestParam Long userId) {
        logger.info("接收到获取用户目标数据的请求，userId: {}", userId);
        
        // 获取用户档案
        YonghuEntity profile = yonghuService.selectById(userId);
        logger.info("获取到用户档案: {}", profile);
        
        // 构建目标数据
        Map<String, Object> goals = new HashMap<>();
        if (profile != null) {
            // 这里可以根据需要从用户档案中提取目标数据
            // 暂时返回空数据，后续可以根据实际需求修改
        }
        
        logger.info("返回的目标数据: {}", goals);
        return R.ok().put("data", goals);
    }
    
    /**
     * 计算每日热量需求
     */
    private int calculateDailyCalories(double weight, double height, int age, String gender, String fitnessGoal) {
        double bmr;
        // Mifflin-St Jeor公式
        if ("男".equals(gender)) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        
        // 根据健身目标调整
        double adjustedCalories;
        switch (fitnessGoal) {
            case "增肌":
                adjustedCalories = bmr * 1.1;
                break;
            case "减脂":
                adjustedCalories = bmr * 0.85;
                break;
            case "维持":
            default:
                adjustedCalories = bmr;
                break;
        }
        
        // 四舍五入取整
        return (int) Math.round(adjustedCalories);
    }
}
