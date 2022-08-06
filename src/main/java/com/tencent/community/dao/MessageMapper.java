package com.tencent.community.dao;

import com.tencent.community.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MessageMapper {

    // 消息列表  显示时候 只显示最新一条信息
    List<Message> selectMessageByUserId(int userId, int offset, int limit);

    // 查询当前用户会话数量
    int selectConversationCount(int userId);

    // 根据会话id， 查询会话中的消息 并分页
    List<Message> selectMessageByConversationId(String conversationId, int offset, int limit);

    // 根据会话id，得出会话中消息数量
    int selectMessageCountByConversationId(String conversationId);

    // 查询未读消息数量
    int selectMessageUnread(int userId, String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改消息状态
    int updateMessage(List<Integer> ids, int status);

    // 查找某一主题的最新通知
    Message findNewMessage(int userId, String topic);

    // 查找某一主题的总数量
    int findTopicMessageCount(int userId, String topic);

    // 查找某一主题的未读数量
    int findTopicMessageUnread(int userId, String topic);


    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);



}
