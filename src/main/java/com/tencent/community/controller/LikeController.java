package com.tencent.community.controller;

import com.tencent.community.domain.Event;
import com.tencent.community.event.EventProducer;
import com.tencent.community.service.LikeService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.HostHolder;
import com.tencent.community.util.JsonUtils;
import com.tencent.community.util.LikeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int postUserId, int postId){

        likeService.updateRedisLikeCount(hostHolder.get().getId(), entityId, entityType, postUserId);
        Map<String, Object> map = new HashMap<>();
        int likeStatus = likeService.likeStatus(hostHolder.get().getId(), entityId, entityType);
        map.put("status", likeStatus);
        map.put("count", likeService.likeCount(entityId, entityType));

        // 发送通知
        if(likeStatus == 1) {
            Event event = new Event();
            event.setTopic(TOPIC_LIKE)
                 .setEntityType(entityType)
                 .setEntityId(entityId)
                 .setUserId(hostHolder.get().getId())
                 .setEntityUserId(postUserId)
                 .setData("postId", postId);
            eventProducer.sendEvent(event);
        }

        if(entityType == ENTITY_TYPE_COMMENT) {
            // 计算帖子分数
            String redisKey = LikeUtils.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }
        return JsonUtils.getJsonString(0, null, map);
    }
}
