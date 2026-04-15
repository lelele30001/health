package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 评论表
 * 
 * @author cl
 * @email 123456789@qq.com
 * @date 2026-04-15 00:00:00
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentEntity> {
    
    /**
     * 根据帖子ID获取评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<CommentEntity> selectByPostId(Long postId);
    
    /**
     * 根据用户ID获取评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    List<CommentEntity> selectByUserId(Long userId);
    
    /**
     * 根据帖子ID和用户ID删除评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 删除成功的数量
     */
    int deleteByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * 根据评论ID和用户ID删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 删除成功的数量
     */
    int deleteByCommentIdAndUserId(Long commentId, Long userId);

}
