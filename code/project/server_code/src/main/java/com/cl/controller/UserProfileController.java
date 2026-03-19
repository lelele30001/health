package com.cl.controller;

import com.cl.entity.UserProfileEntity;
import com.cl.service.UserProfileService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户档案控制器
 */
@RestController
@RequestMapping("/user/profile")
public class UserProfileController {
    
    @Autowired
    private UserProfileService userProfileService;

    /**
     * 获取用户档案
     */
    @GetMapping("/get")
    public R getProfile(@RequestParam Long userId) {
        UserProfileEntity profile = userProfileService.getByUserId(userId);
        return R.ok().put("profile", profile);
    }

    /**
     * 保存/更新用户档案
     */
    @PostMapping("/save")
    public R saveProfile(@RequestBody UserProfileEntity profile) {
        boolean success = userProfileService.saveOrUpdateProfile(profile);
        if (success) {
            return R.ok().put("profile", profile);
        } else {
            return R.error("保存失败");
        }
    }
}
