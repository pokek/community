package com.tencent.community.event;

import com.alibaba.fastjson.JSONObject;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.Event;
import com.tencent.community.domain.Message;
import com.tencent.community.domain.elasticsearch.DiscussPostRepository;
import com.tencent.community.service.DiscussPostService;
import com.tencent.community.service.ElasticsearchService;
import com.tencent.community.service.MessageService;
import com.tencent.community.util.CommunityConstant;
import io.netty.channel.EventLoopGroup;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    MessageService messageService;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    DiscussPostService discussPostService;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleEvent(ConsumerRecord record){
        // 校验参数是否合格
        if(record == null || record.value() == null){
            logger.error("消息为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 解析不成功
        if(event == null){
            logger.error("消息解析不成功");
            return;
        }
        // 封装成message
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setConversationId(record.topic());
        message.setStatus(0);
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());
        // content内容
        Map<String, Object> content = new HashMap<>();
        content.put("entityType", event.getEntityType());
        content.put("userId", event.getUserId());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handleMessagePublished(ConsumerRecord record){
        // 校验参数是否合格
        if(record == null || record.value() == null){
            logger.error("消息为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 解析不成功
        if(event == null){
            logger.error("消息解析不成功");
            return;
        }

        DiscussPost post = discussPostService.findPostById(event.getEntityId());
        elasticsearchService.addPostToEngine(post);
    }


    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        elasticsearchService.deletePostFromEngine(event.getEntityId());
    }

    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功: " + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败: " + e.getMessage());
        }
    }
}
