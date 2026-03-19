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
        return baseMapper.getUserRecipes(userId);
    }

    public List<RecipeEntity> getAllPublicRecipes() {
        return baseMapper.getAllPublicRecipes();
    }

    public RecipeEntity getRecipeById(Long id) {
        return baseMapper.getRecipeWithIngredients(id);
    }
    
    public List<RecipeEntity> getRecommendedRecipes(Long userId, String dietaryRestrictions) {
        return baseMapper.getRecommendedRecipes(userId, dietaryRestrictions);
    }
    
    @Transactional
    public boolean deleteRecipe(Long id) {
        // 先删除关联的食材明细
        recipeIngredientDao.deleteByRecipeId(id);
        // 再删除食谱本身
        return this.deleteById(id);
    }
}