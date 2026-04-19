package com.cl.dao;

import com.cl.entity.MessageEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageDao extends BaseMapper<MessageEntity> {

    /**
     * 获取用户的未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadCount(@Param("userId") Long userId);

    /**
     * 获取用户的消息列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 消息列表
     */
    List<MessageEntity> getMessageList(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     */
    void markAsRead(@Param("messageId") Long messageId);

    /**
     * 标记所有消息为已读
     * @param userId 用户ID
     */
    void markAllAsRead(@Param("userId") Long userId);
}
