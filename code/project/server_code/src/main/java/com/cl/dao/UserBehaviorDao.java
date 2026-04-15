package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.UserBehaviorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorDao extends BaseMapper<UserBehaviorEntity> {
    // 获取用户对特定食谱的行为
    UserBehaviorEntity getUserBehavior(@Param("userId") Long userId, @Param("recipeId") Long recipeId, @Param("behaviorType") String behaviorType);
    
    // 获取用户的所有行为
    List<UserBehaviorEntity> getUserBehaviors(@Param("userId") Long userId);
    
    // 获取食谱的所有行为
    List<UserBehaviorEntity> getRecipeBehaviors(@Param("recipeId") Long recipeId);
    
    // 统计用户行为类型的数量
    Integer countUserBehaviorByType(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);
    
    // 统计食谱行为类型的数量
    Integer countRecipeBehaviorByType(@Param("recipeId") Long recipeId, @Param("behaviorType") String behaviorType);
    
    // 获取用户行为权重统计
    List<Map<String, Object>> getUserBehaviorWeights(@Param("userId") Long userId);
    
    // 获取所有有行为的用户ID列表
    List<Long> getActiveUserIds();
    
    // 获取用户相似的行为（用于协同过滤）
    List<UserBehaviorEntity> getSimilarUserBehaviors(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);
}