package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.UserPostBehaviorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserPostBehaviorDao extends BaseMapper<UserPostBehaviorEntity> {
    
    /**
     * 获取用户对特定帖子的行为
     */
    UserPostBehaviorEntity getUserBehavior(@Param("userId") Long userId, @Param("postId") Long postId, @Param("behaviorType") String behaviorType);
    
    /**
     * 获取用户的所有帖子行为
     */
    List<UserPostBehaviorEntity> getUserBehaviors(@Param("userId") Long userId);
    
    /**
     * 获取帖子的所有行为
     */
    List<UserPostBehaviorEntity> getPostBehaviors(@Param("postId") Long postId);
    
    /**
     * 统计用户行为类型的数量
     */
    Integer countUserBehaviorByType(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);
    
    /**
     * 统计帖子行为类型的数量
     */
    Integer countPostBehaviorByType(@Param("postId") Long postId, @Param("behaviorType") String behaviorType);
    
    /**
     * 获取用户行为权重统计
     */
    List<Map<String, Object>> getUserBehaviorWeights(@Param("userId") Long userId);
    
    /**
     * 获取所有有行为的用户ID列表
     */
    List<Long> getActiveUserIds();
    
    /**
     * 获取用户相似的行为（用于协同过滤）
     */
    List<UserPostBehaviorEntity> getSimilarUserBehaviors(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);
}
