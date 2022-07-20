package com.tencent.community.controller;

import com.tencent.community.annotation.LoginRequired;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.service.DiscussPostService;
import com.tencent.community.util.HostHolder;
import com.tencent.community.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping(value = "/post", method = RequestMethod.POST)
public class DiscussPostController {


    @Autowired
    HostHolder hd;

    @Autowired
    DiscussPostService postService;

    @LoginRequired
    @RequestMapping(value = "/add")
    @ResponseBody
    public String addPost(String title, String text){
        // 初始化post对象
        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(text);
        post.setCreateTime(new Date());
        post.setUserId(String.valueOf(hd.get().getId()));

        // 交给service处理
        postService.addPost(post);

        // 返回json响应数据
        return JsonUtils.getJsonString(200, "帖子发布成功");
    }
}
