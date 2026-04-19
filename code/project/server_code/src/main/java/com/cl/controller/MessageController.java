package com.cl.controller;

import com.cl.service.MessageService;
import com.cl.entity.MessageEntity;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public R getUnreadCount(@RequestParam Long userId) {
        int count = messageService.getUnreadCount(userId);
        return R.ok().put("count", count);
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    public R getMessageList(@RequestParam Long userId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit) {
        List<MessageEntity> list = messageService.getMessageList(userId, page, limit);
        return R.ok().put("list", list);
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/read/{id}")
    public R markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return R.ok();
    }

    /**
     * 标记所有消息为已读
     */
    @PostMapping("/read/all")
    public R markAllAsRead(@RequestParam Long userId) {
        messageService.markAllAsRead(userId);
        return R.ok();
    }

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public R sendMessage(@RequestBody MessageEntity message) {
        if (message.getType() == 1) {
            messageService.sendAuditNotification(message.getUserId(), message.getContent(), message.getRelatedId());
        } else {
            messageService.sendInteractionNotification(message.getUserId(), message.getContent(), message.getRelatedId());
        }
        return R.ok();
    }

}
