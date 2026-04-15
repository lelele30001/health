package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 评论表
 * 
 * @author cl
 * @email 123456789@qq.com
 * @date 2026-04-15 00:00:00
 */
@Data
@TableName("comment")
public class CommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = com.baomidou.mybatisplus.enums.IdType.AUTO)
    private Long id;

    /**
     * 帖子ID
     */
    @TableField("recipe_id")
    private Long recipeId;
    
    /**
     * 帖子ID（用于前端传递）
     */
    @TableField(exist = false)
    private Long postId;
    
    /**
     * 设置帖子ID
     * @param postId 帖子ID
     */
    public void setPostId(Long postId) {
        this.recipeId = postId;
    }
    
    /**
     * 获取帖子ID
     * @return 帖子ID
     */
    public Long getPostId() {
        return this.recipeId;
    }

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

}
