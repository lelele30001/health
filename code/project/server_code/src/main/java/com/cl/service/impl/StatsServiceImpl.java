package com.cl.service.impl;

import com.cl.entity.YonghuEntity;
import com.cl.dao.YonghuDao;
import com.cl.service.StatsService;
import com.cl.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {
    
    @Resource
    private YonghuDao yonghuDao;
    
    @Override
    public R getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总用户数
        int totalUsers = yonghuDao.selectCount(null);
        stats.put("totalUsers", totalUsers);
        
        // 获取今日新增用户数
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int todayUsers = yonghuDao.countByDate(todayStr);
        stats.put("todayUsers", todayUsers);
        
        // 获取本月新增用户数
        String monthStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        int monthUsers = yonghuDao.countByMonth(monthStr);
        stats.put("monthUsers", monthUsers);
        
        // 打印调试信息
        System.out.println("总用户数: " + totalUsers);
        System.out.println("今日新增: " + todayUsers);
        System.out.println("本月新增: " + monthUsers);
        
        return R.ok().put("totalUsers", totalUsers).put("todayUsers", todayUsers).put("monthUsers", monthUsers);
    }
    
    @Override
    public R getUserGenderStats() {
        List<YonghuEntity> users = yonghuDao.selectList(null);
        int maleCount = 0;
        int femaleCount = 0;
        int unknownCount = 0;
        
        for (YonghuEntity user : users) {
            if ("男".equals(user.getXingbie())) {
                maleCount++;
            } else if ("女".equals(user.getXingbie())) {
                femaleCount++;
            } else {
                unknownCount++;
            }
        }
        
        List<Map<String, Object>> genderStats = new ArrayList<>();
        Map<String, Object> maleMap = new HashMap<>();
        maleMap.put("name", "男");
        maleMap.put("value", maleCount);
        genderStats.add(maleMap);
        Map<String, Object> femaleMap = new HashMap<>();
        femaleMap.put("name", "女");
        femaleMap.put("value", femaleCount);
        genderStats.add(femaleMap);
        if (unknownCount > 0) {
            Map<String, Object> unknownMap = new HashMap<>();
            unknownMap.put("name", "未知");
            unknownMap.put("value", unknownCount);
            genderStats.add(unknownMap);
        }
        
        // 打印调试信息
        System.out.println("性别分布 - 男: " + maleCount + ", 女: " + femaleCount + ", 未知: " + unknownCount);
        
        return R.ok().put("data", genderStats);
    }
    
    @Override
    public R getUserAgeStats() {
        List<YonghuEntity> users = yonghuDao.selectList(null);
        int age18_25 = 0;
        int age26_35 = 0;
        int age36_45 = 0;
        int age46Plus = 0;
        int unknownCount = 0;
        
        // 使用用户表中的age字段来计算年龄分布
        for (YonghuEntity user : users) {
            if (user.getAge() != null) {
                int age = user.getAge();
                
                if (age >= 18 && age <= 25) {
                    age18_25++;
                } else if (age >= 26 && age <= 35) {
                    age26_35++;
                } else if (age >= 36 && age <= 45) {
                    age36_45++;
                } else if (age > 45) {
                    age46Plus++;
                } else {
                    unknownCount++;
                }
            } else {
                unknownCount++;
            }
        }
        
        List<Map<String, Object>> ageStats = new ArrayList<>();
        Map<String, Object> age18_25Map = new HashMap<>();
        age18_25Map.put("name", "18-25岁");
        age18_25Map.put("value", age18_25);
        ageStats.add(age18_25Map);
        Map<String, Object> age26_35Map = new HashMap<>();
        age26_35Map.put("name", "26-35岁");
        age26_35Map.put("value", age26_35);
        ageStats.add(age26_35Map);
        Map<String, Object> age36_45Map = new HashMap<>();
        age36_45Map.put("name", "36-45岁");
        age36_45Map.put("value", age36_45);
        ageStats.add(age36_45Map);
        Map<String, Object> age46PlusMap = new HashMap<>();
        age46PlusMap.put("name", "46岁以上");
        age46PlusMap.put("value", age46Plus);
        ageStats.add(age46PlusMap);
        if (unknownCount > 0) {
            Map<String, Object> unknownMap = new HashMap<>();
            unknownMap.put("name", "未知");
            unknownMap.put("value", unknownCount);
            ageStats.add(unknownMap);
        }
        
        // 打印调试信息
        System.out.println("年龄分布 - 18-25岁: " + age18_25 + ", 26-35岁: " + age26_35 + ", 36-45岁: " + age36_45 + ", 46岁以上: " + age46Plus + ", 未知: " + unknownCount);
        
        return R.ok().put("data", ageStats);
    }
    
    @Override
    public R getUserGoalStats() {
        List<YonghuEntity> users = yonghuDao.selectList(null);
        int gainMuscle = 0;
        int loseFat = 0;
        int maintain = 0;
        int unknownCount = 0;
        
        for (YonghuEntity user : users) {
            if (user.getFitnessGoal() != null) {
                switch (user.getFitnessGoal()) {
                    case "增肌":
                        gainMuscle++;
                        break;
                    case "减脂":
                        loseFat++;
                        break;
                    case "维持":
                        maintain++;
                        break;
                    default:
                        unknownCount++;
                        break;
                }
            } else {
                unknownCount++;
            }
        }
        
        List<Map<String, Object>> goalStats = new ArrayList<>();
        Map<String, Object> gainMuscleMap = new HashMap<>();
        gainMuscleMap.put("name", "增肌");
        gainMuscleMap.put("value", gainMuscle);
        goalStats.add(gainMuscleMap);
        Map<String, Object> loseFatMap = new HashMap<>();
        loseFatMap.put("name", "减脂");
        loseFatMap.put("value", loseFat);
        goalStats.add(loseFatMap);
        Map<String, Object> maintainMap = new HashMap<>();
        maintainMap.put("name", "维持");
        maintainMap.put("value", maintain);
        goalStats.add(maintainMap);
        if (unknownCount > 0) {
            Map<String, Object> unknownMap = new HashMap<>();
            unknownMap.put("name", "未知");
            unknownMap.put("value", unknownCount);
            goalStats.add(unknownMap);
        }
        
        // 打印调试信息
        System.out.println("健身目标分布 - 增肌: " + gainMuscle + ", 减脂: " + loseFat + ", 维持: " + maintain + ", 未知: " + unknownCount);
        
        return R.ok().put("data", goalStats);
    }
}
