package com.cl.service;

import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.UserProfileEntity;

/**
 * 用户档案服务
 */
public interface UserProfileService extends IService<UserProfileEntity> {
    
    UserProfileEntity getByUserId(Long userId);
    
    boolean saveOrUpdateProfile(UserProfileEntity profile);
    
    int calculateDailyCalories(double weight, double height, int age, String gender, String fitnessGoal);
}
