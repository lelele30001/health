package com.cl.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.UserProfileDao;
import com.cl.entity.UserProfileEntity;
import com.cl.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户档案服务实现
 */
@Service("userProfileService")
public class UserProfileServiceImpl extends ServiceImpl<UserProfileDao, UserProfileEntity> implements UserProfileService {
    
    @Autowired
    private UserProfileDao userProfileDao;

    @Override
    public UserProfileEntity getByUserId(Long userId) {
        return userProfileDao.getByUserId(userId);
    }

    @Override
    public boolean saveOrUpdateProfile(UserProfileEntity profile) {
        // 计算每日热量需求
        int dailyCalories = calculateDailyCalories(
            profile.getWeight(),
            profile.getHeight(),
            profile.getAge(),
            profile.getGender(),
            profile.getFitnessGoal()
        );
        profile.setDailyCalories(dailyCalories);
        
        // 检查是否已存在档案
        UserProfileEntity existing = userProfileDao.getByUserId(profile.getUserId());
        if (existing != null) {
            // 更新现有档案
            profile.setId(existing.getId());
            return this.updateById(profile);
        } else {
            // 保存新档案
            return this.insert(profile);
        }
    }

    @Override
    public int calculateDailyCalories(double weight, double height, int age, String gender, String fitnessGoal) {
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
