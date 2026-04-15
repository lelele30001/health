package com.cl.service;

import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.UserDailyRecordEntity;

import java.util.List;

/**
 * 每日体重记录Service
 */
public interface UserDailyRecordService extends IService<UserDailyRecordEntity> {
    
    /**
     * 根据用户ID获取每日体重记录
     */
    List<UserDailyRecordEntity> getByUserId(Long userId);
    
    /**
     * 保存或更新每日体重记录
     */
    boolean saveOrUpdateRecord(UserDailyRecordEntity record);
    
    /**
     * 删除每日体重记录
     */
    boolean deleteRecord(String id);
    
    /**
     * 删除用户的所有每日体重记录
     */
    boolean deleteRecordsByUserId(Long userId);
}
