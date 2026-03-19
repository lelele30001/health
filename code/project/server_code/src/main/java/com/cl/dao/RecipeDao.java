package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.RecipeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeDao extends BaseMapper<RecipeEntity> {
    List<RecipeEntity> getUserRecipes(@Param("userId") Long userId);
    List<RecipeEntity> getAllPublicRecipes();
    RecipeEntity getRecipeWithIngredients(@Param("id") Long id);
    List<RecipeEntity> getRecommendedRecipes(@Param("userId") Long userId, @Param("dietaryRestrictions") String dietaryRestrictions);
}