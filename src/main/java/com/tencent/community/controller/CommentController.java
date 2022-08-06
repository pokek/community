package com.tencent.community.controller;

import com.tencent.community.domain.Comment;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.Event;
import com.tencent.community.event.EventProducer;
import com.tencent.community.service.CommentService;
import com.tencent.community.service.DiscussPostService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.HostHolder;
import com.tencent.community.util.LikeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.HashMap;

@Controller
@RequestMapping(value = "/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/add/{postId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment, Model model){
        if(StringUtils.isBlank(comment.getContent())){
            throw new IllegalArgumentException("评论内容不能为空");
        }
        comment.setUserId(hostHolder.get().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        // 系统发送通知
        // 封装event
        Event event = new Event();
        event.setUserId(hostHolder.get().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setTopic(TOPIC_COMMENT)
                .setData("postId", postId);

        if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            DiscussPost discussPost = discussPostService.findPostById(comment.getEntityId());
            event.setEntityUserId(Integer.parseInt(discussPost.getUserId()));
        }else if(comment.getEntityType() == ENTITY_TYPE_REPLY){
            // 回复种类有两种，一个是回复指定的用户评论，一个是回复不指定的用户评论
            Comment com = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(com.getUserId());
        }

        eventProducer.sendEvent(event);

        // 帖子评论数量更新事件，需要覆盖搜索引擎里的数据
        if(comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            event = new Event();
            event.setTopic(TOPIC_PUBLISH)
                 .setUserId(comment.getUserId())
                 .setEntityId(postId)
                 .setEntityType(ENTITY_TYPE_COMMENT);
            eventProducer.sendEvent(event);

            // 计算帖子分数
            String redisKey = LikeUtils.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return "redirect:/post/detail/" + postId;

    }
}
