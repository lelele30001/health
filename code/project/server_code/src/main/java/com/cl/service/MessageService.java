package com.cl.service;

import com.cl.entity.MessageEntity;

import java.util.List;

public interface MessageService {

    /**
     * 发送审核通知
     * @param userId 用户ID
     * @param content 消息内容
     * @param relatedId 关联对象ID（食谱ID或评论ID）
     */
    void sendAuditNotification(Long userId, String content, Long relatedId);

    /**
     * 发送互动通知
     * @param userId 用户ID
     * @param content 消息内容
     * @param relatedId 关联对象ID（食谱ID或评论ID）
     */
    void sendInteractionNotification(Long userId, String content, Long relatedId);

    /**
     * 获取用户的未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadCount(Long userId);

    /**
     * 获取用户的消息列表
     * @param userId 用户ID
     * @param page 页码
     * @param limit 每页数量
     * @return 消息列表
     */
    List<MessageEntity> getMessageList(Long userId, int page, int limit);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     */
    void markAsRead(Long messageId);

    /**
     * 标记所有消息为已读
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);
}
