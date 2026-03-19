package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.RecipeRecommendationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeRecommendationDao extends BaseMapper<RecipeRecommendationEntity> {
    // 获取用户的推荐食谱列表
    List<RecipeRecommendationEntity> getRecommendationsByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    // 获取推荐分数大于某个值的食谱
    List<RecipeRecommendationEntity> getRecommendationsByScore(@Param("userId") Long userId, @Param("minScore") Double minScore, @Param("limit") Integer limit);
    
    // 删除用户的所有推荐
    void deleteRecommendationsByUserId(@Param("userId") Long userId);
    
    // 检查是否已存在推荐
    RecipeRecommendationEntity getRecommendation(@Param("userId") Long userId, @Param("recipeId") Long recipeId);
}