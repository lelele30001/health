package com.cl.service.impl;

import com.cl.dao.MessageDao;
import com.cl.entity.MessageEntity;
import com.cl.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Override
    public void sendAuditNotification(Long userId, String content, Long relatedId) {
        MessageEntity message = new MessageEntity();
        message.setUserId(userId);
        message.setType(1); // 1=审核通知
        message.setContent(content);
        message.setRelatedId(relatedId);
        message.setStatus(0); // 0=未读
        messageDao.insert(message);
    }

    @Override
    public void sendInteractionNotification(Long userId, String content, Long relatedId) {
        MessageEntity message = new MessageEntity();
        message.setUserId(userId);
        message.setType(2); // 2=互动通知
        message.setContent(content);
        message.setRelatedId(relatedId);
        message.setStatus(0); // 0=未读
        messageDao.insert(message);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return messageDao.getUnreadCount(userId);
    }

    @Override
    public List<MessageEntity> getMessageList(Long userId, int page, int limit) {
        return messageDao.getMessageList(userId, (page - 1) * limit, limit);
    }

    @Override
    public void markAsRead(Long messageId) {
        messageDao.markAsRead(messageId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        messageDao.markAllAsRead(userId);
    }
}
