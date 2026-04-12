package com.cl.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.UserPostBehaviorDao;
import com.cl.dao.StoreupDao;
import com.cl.dao.ForumDao;
import com.cl.entity.UserPostBehaviorEntity;
import com.cl.entity.StoreupEntity;
import com.cl.entity.ForumEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Service("userPostBehaviorService")
public class UserPostBehaviorService extends ServiceImpl<UserPostBehaviorDao, UserPostBehaviorEntity> {

    private static final Logger logger = LoggerFactory.getLogger(UserPostBehaviorService.class);

    @Autowired
    private UserPostBehaviorDao userPostBehaviorDao;

    @Autowired
    private StoreupDao storeupDao;

    @Autowired
    private ForumDao forumDao;

    /**
     * 记录用户帖子行为
     */
    @Transactional
    public boolean recordUserBehavior(UserPostBehaviorEntity behavior) {
        try {
            // 检查是否已存在相同行为
            UserPostBehaviorEntity existingBehavior = userPostBehaviorDao.getUserBehavior(
                    behavior.getUserId(), behavior.getPostId(), behavior.getBehaviorType());
            
            if (existingBehavior != null) {
                // 更新行为时间
                existingBehavior.setBehaviorTime(new Date());
                this.updateById(existingBehavior);
            } else {
                // 记录新行为
                behavior.setBehaviorTime(new Date());
                this.insert(behavior);
            }
            
            // 如果是收藏行为，同步到 storeup 表
            if ("collect".equals(behavior.getBehaviorType())) {
                syncToStoreup(behavior.getUserId(), behavior.getPostId());
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除用户帖子行为
     */
    @Transactional
    public boolean deleteUserBehavior(Long userId, Long postId, String behaviorType) {
        try {
            // 删除行为记录
            UserPostBehaviorEntity behavior = userPostBehaviorDao.getUserBehavior(userId, postId, behaviorType);
            if (behavior != null) {
                this.deleteById(behavior.getId());
            }
            
            // 如果是取消收藏，从 storeup 表中删除
            if ("collect".equals(behaviorType)) {
                removeFromStoreup(userId, postId);
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 同步收藏行为到 storeup 表
     */
    private void syncToStoreup(Long userId, Long postId) {
        try {
            logger.info("开始同步收藏行为，用户ID: {}, 帖子ID: {}", userId, postId);
            
            // 获取帖子信息
            ForumEntity forum = forumDao.selectById(postId);
            if (forum != null) {
                logger.info("获取到帖子信息，标题: {}", forum.getTitle());
                
                StoreupEntity storeup = new StoreupEntity();
                storeup.setUserid(userId);
                storeup.setRefid(postId);
                storeup.setTablename("forum");
                storeup.setName(forum.getTitle());
                storeup.setPicture(""); // 帖子没有图片字段
                storeup.setType("1"); // 1表示收藏
                storeup.setAddtime(new Date());
                try {
                    storeupDao.insert(storeup);
                    logger.info("收藏行为同步成功，用户ID: {}, 帖子ID: {}", userId, postId);
                } catch (Exception e) {
                    logger.info("收藏行为已存在，跳过同步，用户ID: {}, 帖子ID: {}", userId, postId);
                    // 忽略重复插入的异常
                }
            } else {
                logger.warn("帖子不存在，无法同步收藏行为，帖子ID: {}", postId);
            }
        } catch (Exception e) {
            logger.error("同步收藏行为失败，用户ID: {}, 帖子ID: {}", userId, postId, e);
            e.printStackTrace();
        }
    }

    /**
     * 从 storeup 表中删除收藏记录
     */
    private void removeFromStoreup(Long userId, Long postId) {
        try {
            // 使用 Map 来构建删除条件
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("userid", userId);
            map.put("refid", postId);
            map.put("tablename", "forum");
            map.put("type", "1");
            storeupDao.deleteByMap(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户对特定帖子的行为
     */
    public UserPostBehaviorEntity getUserBehavior(Long userId, Long postId, String behaviorType) {
        return userPostBehaviorDao.getUserBehavior(userId, postId, behaviorType);
    }

    /**
     * 获取用户的所有帖子行为
     */
    public List<UserPostBehaviorEntity> getUserBehaviors(Long userId) {
        return userPostBehaviorDao.getUserBehaviors(userId);
    }

    /**
     * 获取帖子的所有行为
     */
    public List<UserPostBehaviorEntity> getPostBehaviors(Long postId) {
        return userPostBehaviorDao.getPostBehaviors(postId);
    }

    /**
     * 统计用户行为类型的数量
     */
    public Integer countUserBehaviorByType(Long userId, String behaviorType) {
        return userPostBehaviorDao.countUserBehaviorByType(userId, behaviorType);
    }

    /**
     * 统计帖子行为类型的数量
     */
    public Integer countPostBehaviorByType(Long postId, String behaviorType) {
        return userPostBehaviorDao.countPostBehaviorByType(postId, behaviorType);
    }

    /**
     * 同步所有收藏行为到 storeup 表
     * 用于处理通过 SQL 直接插入的收藏行为
     */
    public void syncAllCollectBehaviors() {
        try {
            logger.info("开始同步所有收藏行为到 storeup 表");
            
            // 获取所有有行为的用户ID
            List<Long> userIds = userPostBehaviorDao.getActiveUserIds();
            logger.info("获取到 {} 个活跃用户", userIds.size());
            
            // 遍历所有用户，获取他们的收藏行为
            for (Long userId : userIds) {
                logger.info("处理用户ID: {}", userId);
                List<UserPostBehaviorEntity> behaviors = userPostBehaviorDao.getUserBehaviors(userId);
                logger.info("用户 {} 有 {} 个行为记录", userId, behaviors.size());
                
                for (UserPostBehaviorEntity behavior : behaviors) {
                    if ("collect".equals(behavior.getBehaviorType())) {
                        syncToStoreup(behavior.getUserId(), behavior.getPostId());
                    }
                }
            }
            
            logger.info("所有收藏行为同步完成");
        } catch (Exception e) {
            logger.error("同步收藏行为失败", e);
            e.printStackTrace();
        }
    }
}
