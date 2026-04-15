package com.cl.controller;

import com.cl.entity.UserDailyRecordEntity;
import com.cl.service.UserDailyRecordService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 每日体重记录控制器
 */
@RestController
@RequestMapping("/user-daily-record")
public class UserDailyRecordController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDailyRecordController.class);
    
    @Autowired
    private UserDailyRecordService userDailyRecordService;

    /**
     * 根据用户ID获取每日体重记录
     */
    @GetMapping("/list")
    public R getList(@RequestParam Long userId) {
        logger.info("接收到获取用户每日体重记录的请求，userId: {}", userId);
        
        List<UserDailyRecordEntity> records = userDailyRecordService.getByUserId(userId);
        logger.info("获取到用户每日体重记录: {}", records);
        
        return R.ok().put("records", records);
    }

    /**
     * 保存或更新每日体重记录
     */
    @PostMapping("/save")
    public R saveRecord(@RequestBody UserDailyRecordEntity record) {
        logger.info("接收到保存每日体重记录的请求: {}", record);
        
        boolean success = userDailyRecordService.saveOrUpdateRecord(record);
        if (success) {
            return R.ok().put("record", record);
        } else {
            return R.error("保存失败");
        }
    }

    /**
     * 删除每日体重记录
     */
    @DeleteMapping("/delete")
    public R deleteRecord(@RequestParam String id) {
        logger.info("接收到删除每日体重记录的请求，id: {}", id);
        
        boolean success = userDailyRecordService.deleteRecord(id);
        if (success) {
            return R.ok();
        } else {
            return R.error("删除失败");
        }
    }

    /**
     * 删除用户的所有每日体重记录
     */
    @DeleteMapping("/delete-all")
    public R deleteRecordsByUserId(@RequestParam Long userId) {
        logger.info("接收到删除用户所有每日体重记录的请求，userId: {}", userId);
        
        boolean success = userDailyRecordService.deleteRecordsByUserId(userId);
        if (success) {
            return R.ok();
        } else {
            return R.error("删除失败");
        }
    }
}
