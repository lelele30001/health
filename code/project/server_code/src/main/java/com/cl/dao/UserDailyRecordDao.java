package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.UserDailyRecordEntity;

import java.util.List;

/**
 * 每日体重记录Dao
 */
public interface UserDailyRecordDao extends BaseMapper<UserDailyRecordEntity> {
    
    /**
     * 根据用户ID获取每日体重记录
     */
    List<UserDailyRecordEntity> getByUserId(Long userId);
    
    /**
     * 根据用户ID和日期获取记录
     */
    UserDailyRecordEntity getByUserIdAndDate(Long userId, String recordDate);
}
