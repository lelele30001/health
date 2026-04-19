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
        // 从会话中获取当前用户的用户名和ID
        String username = (String) request.getSession().getAttribute("username");
        Long userId = (Long) request.getSession().getAttribute("userId");
        recipe.setUsername(username);
        recipe.setUserId(userId);
        // 设置状态为待审核
        recipe.setStatus("pending");
        
        boolean saved = recipeService.saveRecipeWithIngredients(recipe);
        if (saved) {
            return R.ok().put("recipe", recipe).put("message", "食谱已提交，等待审核");
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
    
    @GetMapping("/all")
    public R getAllRecipes() {
        List<RecipeEntity> recipes = recipeService.getAllRecipes();
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
    public R deleteRecipe(@PathVariable Long id, HttpServletRequest request, @RequestParam(required = false) Long userId) {
        // 优先从请求参数获取用户ID，如果没有则从会话中获取
        if (userId == null) {
            userId = (Long) request.getSession().getAttribute("userId");
            if (userId == null) {
                return R.error("用户未登录");
            }
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
    
    @PostMapping("/update")
    public R updateRecipe(@RequestBody RecipeEntity recipe, HttpServletRequest request) {
        // 从请求体中获取用户ID，如果没有则从会话中获取
        Long userId = recipe.getUserId();
        if (userId == null) {
            userId = (Long) request.getSession().getAttribute("userId");
            if (userId == null) {
                return R.error("用户未登录");
            }
        }
        
        // 检查食谱是否存在且属于当前用户
        RecipeEntity existingRecipe = recipeService.getRecipeById(recipe.getId());
        if (existingRecipe == null) {
            return R.error("食谱不存在");
        }
        
        if (!existingRecipe.getUserId().equals(userId)) {
            return R.error("无权修改该食谱");
        }
        
        // 更新食谱
        boolean updated = recipeService.updateRecipe(recipe);
        if (updated) {
            return R.ok().put("recipe", recipe);
        } else {
            return R.error("更新失败");
        }
    }
    
    @PostMapping("/delete")
    public R batchDeleteRecipes(@RequestBody List<Long> ids, HttpServletRequest request) {
        // 从会话中获取当前用户的ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("用户未登录");
        }
        
        // 批量删除食谱
        boolean deleted = recipeService.batchDeleteRecipes(ids);
        if (deleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
    
    @PostMapping("/updateStatus")
    public R updateRecipeStatus(@RequestBody RecipeEntity recipe) {
        // 更新食谱状态
        boolean updated = recipeService.updateById(recipe);
        if (updated) {
            return R.ok("状态更新成功");
        } else {
            return R.error("状态更新失败");
        }
    }
}