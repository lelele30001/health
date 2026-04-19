package com.cl.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.CommentDao;
import com.cl.entity.CommentEntity;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 评论表
 * 
 * @author cl
 * @email 123456789@qq.com
 * @date 2026-04-15 00:00:00
 */
@Service("commentService")
public class CommentService extends ServiceImpl<CommentDao, CommentEntity> {
    
    /**
     * 根据帖子ID获取评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    public List<CommentEntity> getCommentsByPostId(Long postId) {
        return baseMapper.selectByPostId(postId);
    }
    
    /**
     * 根据用户ID获取评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    public List<CommentEntity> getCommentsByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
    
    /**
     * 发表评论
     * @param comment 评论实体
     * @return 操作结果
     */
    public boolean addComment(CommentEntity comment) {
        // 设置状态为待审核
        comment.setStatus("pending");
        return this.insert(comment);
    }
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 操作结果
     */
    public boolean deleteComment(Long commentId, Long userId) {
        int result = baseMapper.deleteByCommentIdAndUserId(commentId, userId);
        return result > 0;
    }
    
    /**
     * 删除帖子的所有评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 操作结果
     */
    public boolean deleteCommentsByPostId(Long postId, Long userId) {
        int result = baseMapper.deleteByPostIdAndUserId(postId, userId);
        return result > 0;
    }
    
    /**
     * 获取所有评论
     * @return 所有评论列表
     */
    public List<CommentEntity> getAllComments() {
        return this.selectList(null);
    }

}
