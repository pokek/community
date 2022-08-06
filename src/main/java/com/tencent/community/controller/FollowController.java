package com.tencent.community.controller;

import com.tencent.community.domain.Event;
import com.tencent.community.domain.Page;
import com.tencent.community.domain.User;
import com.tencent.community.event.EventProducer;
import com.tencent.community.service.FollowService;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.HostHolder;
import com.tencent.community.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    HostHolder hd;

    @Autowired
    FollowService followService;

    @Autowired
    UserService us;

    @Autowired
    EventProducer eventProducer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        int userId = hd.get().getId();
        followService.follow(userId, entityId, entityType);

        Event event = new Event();
        event.setTopic(TOPIC_FOLLOW)
                .setUserId(userId)
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setEntityUserId(entityId);
        eventProducer.sendEvent(event);
        return JsonUtils.getJsonString(0, "已关注");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        int userId = hd.get().getId();
        followService.unfollow(userId, entityId, entityType);
        return JsonUtils.getJsonString(0, "已取消关注");
    }

    @GetMapping("/followees/{userid}")
    public String followees(@PathVariable("userid") int userId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int)followService.followeeCount(userId, CommunityConstant.ENTITY_TYPE_USER));
        User u = hd.get();
        List<Map<String, Object>> mapList = followService.getFolloweeUser(userId, page.getStart(), page.getLimit());
        for(Map<String, Object> map : mapList){
            User followeeUser = (User) map.get("followeeuser");
            boolean isFollow = false;
            if(u != null){
                isFollow = followService.isFollowed(CommunityConstant.ENTITY_TYPE_USER, followeeUser.getId());
            }
            map.put("isfollow", isFollow);
        }
        model.addAttribute("fesvo", mapList);
        model.addAttribute("user", us.findUserById(userId));
        return "site/followee";
    }


    @GetMapping("/followers/{userid}")
    public String followers(@PathVariable("userid") int userId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int)followService.followerCount(CommunityConstant.ENTITY_TYPE_USER, userId));
        User u = hd.get();
        List<Map<String, Object>> mapList = followService.getFollowUser(userId, page.getStart(), page.getLimit());
        for(Map<String, Object> map : mapList){
            User followUser = (User) map.get("followuser");
            boolean isFollow = false;
            if(u != null){
                isFollow = followService.isFollowed(CommunityConstant.ENTITY_TYPE_USER, followUser.getId());
            }
            map.put("isfollow", isFollow);
        }
        model.addAttribute("frsvo", mapList);
        model.addAttribute("user", us.findUserById(userId));
        return "site/follower";
    }
}


