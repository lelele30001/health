package com.cl.controller;

import com.cl.entity.RecipeEntity;
import com.cl.service.RecipeService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("/publish")
    public R publishRecipe(@RequestBody RecipeEntity recipe, HttpServletRequest request) {
        // 从会话中获取当前用户的用户名
        String username = (String) request.getSession().getAttribute("username");
        recipe.setUsername(username);
        
        boolean saved = recipeService.saveRecipeWithIngredients(recipe);
        if (saved) {
            return R.ok().put("recipe", recipe);
        } else {
            return R.error("发布失败");
        }
    }

    @GetMapping("/user/{userId}")
    public R getUserRecipes(@PathVariable Long userId) {
        List<RecipeEntity> recipes = recipeService.getUserRecipes(userId);
        return R.ok().put("recipes", recipes);
    }

    @GetMapping("/public")
    public R getAllPublicRecipes() {
        List<RecipeEntity> recipes = recipeService.getAllPublicRecipes();
        return R.ok().put("recipes", recipes);
    }

    @GetMapping("/info/{id}")
    public R getRecipeInfo(@PathVariable Long id) {
        RecipeEntity recipe = recipeService.getRecipeById(id);
        if (recipe != null) {
            return R.ok().put("recipe", recipe);
        } else {
            return R.error("食谱不存在");
        }
    }
    
    @GetMapping("/recommend")
    public R getRecommendedRecipes(@RequestParam Long userId, @RequestParam(required = false) String dietaryRestrictions) {
        List<RecipeEntity> recipes = recipeService.getRecommendedRecipes(userId, dietaryRestrictions);
        return R.ok().put("recipes", recipes);
    }
    
    @DeleteMapping("/delete/{id}")
    public R deleteRecipe(@PathVariable Long id, HttpServletRequest request) {
        // 从会话中获取当前用户的ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 检查食谱是否存在且属于当前用户
        RecipeEntity recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return R.error("食谱不存在");
        }
        
        if (!recipe.getUserId().equals(userId)) {
            return R.error("无权删除该食谱");
        }
        
        // 删除食谱（这里需要在RecipeService中实现删除方法）
        boolean deleted = recipeService.deleteRecipe(id);
        if (deleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}