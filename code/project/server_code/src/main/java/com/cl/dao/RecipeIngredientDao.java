package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.RecipeIngredientEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecipeIngredientDao extends BaseMapper<RecipeIngredientEntity> {
    // 根据食谱ID删除所有食材明细
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}