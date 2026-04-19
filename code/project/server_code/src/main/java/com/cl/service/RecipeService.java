package com.cl.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.RecipeDao;
import com.cl.dao.RecipeIngredientDao;
import com.cl.entity.RecipeEntity;
import com.cl.entity.RecipeIngredientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("recipeService")
public class RecipeService extends ServiceImpl<RecipeDao, RecipeEntity> {

    @Autowired
    private RecipeIngredientDao recipeIngredientDao;

    @Transactional
    public boolean saveRecipeWithIngredients(RecipeEntity recipe) {
        // 保存食谱主表
        boolean saved = this.insert(recipe);
        if (saved && recipe.getIngredients() != null) {
            // 保存食材明细
            for (RecipeIngredientEntity ingredient : recipe.getIngredients()) {
                ingredient.setRecipeId(recipe.getId());
                recipeIngredientDao.insert(ingredient);
            }
        }
        return saved;
    }

    public List<RecipeEntity> getUserRecipes(Long userId) {
        List<RecipeEntity> recipes = baseMapper.getUserRecipes(userId);
        return recipes;
    }

    public List<RecipeEntity> getAllPublicRecipes() {
        List<RecipeEntity> recipes = baseMapper.getAllPublicRecipes();
        return recipes;
    }

    public RecipeEntity getRecipeById(Long id) {
        RecipeEntity recipe = baseMapper.getRecipeWithIngredients(id);
        return recipe;
    }
    
    public List<RecipeEntity> getRecommendedRecipes(Long userId, String dietaryRestrictions) {
        List<RecipeEntity> recipes = baseMapper.getRecommendedRecipes(userId, dietaryRestrictions);
        return recipes;
    }
    
    @Transactional
    public boolean deleteRecipe(Long id) {
        // 先删除关联的食材明细
        recipeIngredientDao.deleteByRecipeId(id);
        // 再删除食谱本身
        return this.deleteById(id);
    }
    
    @Transactional
    public boolean updateRecipe(RecipeEntity recipe) {
        // 更新食谱主表
        boolean updated = this.updateById(recipe);
        if (updated && recipe.getIngredients() != null) {
            // 先删除原有的食材明细
            recipeIngredientDao.deleteByRecipeId(recipe.getId());
            // 再保存新的食材明细
            for (RecipeIngredientEntity ingredient : recipe.getIngredients()) {
                ingredient.setRecipeId(recipe.getId());
                recipeIngredientDao.insert(ingredient);
            }
        }
        return updated;
    }
    
    @Transactional
    public boolean batchDeleteRecipes(List<Long> ids) {
        // 批量删除每个食谱的关联食材明细
        for (Long id : ids) {
            recipeIngredientDao.deleteByRecipeId(id);
        }
        // 批量删除食谱本身
        return this.deleteBatchIds(ids);
    }
    
    public List<RecipeEntity> getAllRecipes() {
        return this.selectList(null);
    }
}