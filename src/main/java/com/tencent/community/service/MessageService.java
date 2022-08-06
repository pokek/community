package com.tencent.community.service;

import com.tencent.community.dao.MessageMapper;
import com.tencent.community.domain.Message;
import com.tencent.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    public List<Message> findSession(int userId, int offset, int limit){
        return messageMapper.selectMessageByUserId(userId, offset, limit);
    }

    public int countSession(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findOnSessionMessage(String sessionId, int offset, int limit){
        return messageMapper.selectMessageByConversationId(sessionId, offset, limit);
    }

    public int findCountOnSessionMessage(String sessionId){
        return messageMapper.selectMessageCountByConversationId(sessionId);
    }

    public int findUnreadMessage(int userId, String sessionId){
        return messageMapper.selectMessageUnread(userId, sessionId);
    }

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int updateMessage(List<Integer> ids, int status){
        return messageMapper.updateMessage(ids, status);
    }

    public Message findNewMessage(int userId, String topic){
        return messageMapper.findNewMessage(userId, topic);
    }

    public int findTopicMessageCount(int userId, String topic){
        return messageMapper.findTopicMessageCount(userId, topic);
    }

    public int findTopicMessageUnread(int userId, String topic){
        return messageMapper.findTopicMessageUnread(userId, topic);
    }


    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}

/*
    根据需求分层处理，网页需求定sql语句，sql语句参数根据网页需求的实际需要来给，表现层处理与页面交互的一些逻辑和数据，业务层处理与数据库打交道的数据，如用户数据等数据等，
 */
