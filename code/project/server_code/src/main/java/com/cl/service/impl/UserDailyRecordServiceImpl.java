package com.cl.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.UserDailyRecordDao;
import com.cl.entity.UserDailyRecordEntity;
import com.cl.service.UserDailyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 每日体重记录Service实现
 */
@Service("userDailyRecordService")
public class UserDailyRecordServiceImpl extends ServiceImpl<UserDailyRecordDao, UserDailyRecordEntity> implements UserDailyRecordService {
    
    @Autowired
    private UserDailyRecordDao userDailyRecordDao;
    
    @Override
    public List<UserDailyRecordEntity> getByUserId(Long userId) {
        return userDailyRecordDao.getByUserId(userId);
    }
    
    @Override
    public boolean saveOrUpdateRecord(UserDailyRecordEntity record) {
        try {
            // 先检查是否存在相同日期的记录
            // 使用正确的日期格式
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String recordDateStr = sdf.format(record.getRecordDate());
            
            System.out.println("保存记录 - 用户ID: " + record.getUserId());
            System.out.println("保存记录 - 日期: " + recordDateStr);
            System.out.println("保存记录 - 体重: " + record.getWeight());
            System.out.println("保存记录 - BMI: " + record.getBmi());
            
            UserDailyRecordEntity existingRecord = userDailyRecordDao.getByUserIdAndDate(
                record.getUserId(), 
                recordDateStr
            );
            
            if (existingRecord != null) {
                // 如果存在，更新记录
                System.out.println("更新现有记录，ID: " + existingRecord.getId());
                record.setId(existingRecord.getId());
            } else {
                // 新记录
                System.out.println("创建新记录");
            }
            
            // 确保日期格式正确
            java.util.Date formattedDate = sdf.parse(recordDateStr);
            record.setRecordDate(formattedDate);
            
            boolean result = this.insertOrUpdate(record);
            System.out.println("保存结果: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("保存记录失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteRecord(String id) {
        return this.deleteById(id);
    }
    
    @Override
    public boolean deleteRecordsByUserId(Long userId) {
        // 这里可以根据需要实现删除用户所有记录的逻辑
        // 暂时返回true，实际项目中需要实现具体的删除逻辑
        return true;
    }
}
