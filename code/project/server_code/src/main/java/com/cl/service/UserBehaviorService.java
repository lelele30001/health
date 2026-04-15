package com.cl.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.UserBehaviorDao;
import com.cl.entity.UserBehaviorEntity;
import com.cl.entity.RecipeEntity;
import com.cl.entity.StoreupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("userBehaviorService")
public class UserBehaviorService extends ServiceImpl<UserBehaviorDao, UserBehaviorEntity> {

    @Autowired
    private UserBehaviorDao userBehaviorDao;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private StoreupService storeupService;

    // 记录用户行为
    @Transactional
    public boolean recordUserBehavior(UserBehaviorEntity behavior) {
        // 检查行为类型是否为空或无效
        if (behavior.getBehaviorType() == null || behavior.getBehaviorType().isEmpty()) {
            return false;
        }
        
        // 检查是否已存在相同行为
        UserBehaviorEntity existingBehavior = userBehaviorDao.getUserBehavior(
                behavior.getUserId(), behavior.getRecipeId(), behavior.getBehaviorType());
        
        if (existingBehavior != null) {
            // 更新已存在的行为
            existingBehavior.setBehaviorTime(new Date());
            return this.updateById(existingBehavior);
        } else {
            // 创建新行为
            behavior.setBehaviorTime(new Date());
            boolean inserted = this.insert(behavior);
            
            // 如果是收藏行为，同时更新storeup表
            if (inserted && "collect".equals(behavior.getBehaviorType())) {
                RecipeEntity recipe = recipeService.selectById(behavior.getRecipeId());
                if (recipe != null) {
                    StoreupEntity storeup = new StoreupEntity();
                    storeup.setRefid(behavior.getRecipeId());
                    storeup.setTablename("recipe");
                    storeup.setName(recipe.getTitle());
                    storeup.setPicture(recipe.getCoverImage());
                    storeup.setType("1"); // 1:收藏
                    storeup.setUserid(behavior.getUserId());
                    storeupService.insert(storeup);
                }
            }
            
            return inserted;
        }
    }

    // 获取用户对特定食谱的行为
    public UserBehaviorEntity getUserBehavior(Long userId, Long recipeId, String behaviorType) {
        return userBehaviorDao.getUserBehavior(userId, recipeId, behaviorType);
    }

    // 获取用户的所有行为
    public List<UserBehaviorEntity> getUserBehaviors(Long userId) {
        return userBehaviorDao.getUserBehaviors(userId);
    }

    // 获取食谱的所有行为
    public List<UserBehaviorEntity> getRecipeBehaviors(Long recipeId) {
        return userBehaviorDao.getRecipeBehaviors(recipeId);
    }

    // 统计用户行为类型的数量
    public Integer countUserBehaviorByType(Long userId, String behaviorType) {
        return userBehaviorDao.countUserBehaviorByType(userId, behaviorType);
    }

    // 统计食谱行为类型的数量
    public Integer countRecipeBehaviorByType(Long recipeId, String behaviorType) {
        return userBehaviorDao.countRecipeBehaviorByType(recipeId, behaviorType);
    }

    // 获取用户行为权重统计
    public List<Map<String, Object>> getUserBehaviorWeights(Long userId) {
        return userBehaviorDao.getUserBehaviorWeights(userId);
    }

    // 删除用户行为
    @Transactional
    public boolean deleteUserBehavior(Long userId, Long recipeId, String behaviorType) {
        UserBehaviorEntity behavior = userBehaviorDao.getUserBehavior(userId, recipeId, behaviorType);
        if (behavior != null) {
            boolean deleted = this.deleteById(behavior.getId());
            
            // 如果是收藏行为，同时从storeup表中删除
            if (deleted && "collect".equals(behaviorType)) {
                StoreupEntity storeup = storeupService.selectOne(
                        new EntityWrapper<StoreupEntity>()
                                .eq("refid", recipeId)
                                .eq("tablename", "recipe")
                                .eq("userid", userId)
                );
                if (storeup != null) {
                    storeupService.deleteById(storeup.getId());
                }
            }
            
            return deleted;
        }
        return false;
    }
}