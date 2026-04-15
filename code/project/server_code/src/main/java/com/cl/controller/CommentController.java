package com.cl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cl.entity.CommentEntity;
import com.cl.service.CommentService;
import com.cl.utils.R;

/**
 * 评论表
 * 
 * @author cl
 * @email 123456789@qq.com
 * @date 2026-04-15 00:00:00
 */
@RestController
@RequestMapping("comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 根据帖子ID获取评论列表
     */
    @RequestMapping("/list/{postId}")
    public R list(@PathVariable Long postId) {
        return R.ok().put("data", commentService.getCommentsByPostId(postId));
    }

    /**
     * 发表评论
     */
    @RequestMapping("/add")
    public R add(@RequestBody CommentEntity comment) {
        boolean result = commentService.addComment(comment);
        if (result) {
            return R.ok().put("comment", comment);
        } else {
            return R.error("评论失败");
        }
    }

    /**
     * 删除评论
     */
    @RequestMapping("/delete/{id}")
    public R delete(@PathVariable Long id, @RequestParam Long userId) {
        boolean result = commentService.deleteComment(id, userId);
        if (result) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

}
