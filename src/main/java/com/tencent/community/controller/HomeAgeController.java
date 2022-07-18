package com.tencent.community.controller;

import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.Page;
import com.tencent.community.domain.User;
import com.tencent.community.service.DiscussPostService;
import com.tencent.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class HomeAgeController {

    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String homeAge(Model model, Page page){
        page.setRows(discussPostService.findAllPost(0));
        page.setPath("/index");
        List<DiscussPost> discussPosts = discussPostService.discussPostByUserId(0, page.getStart(), page.getLimit());
        List<Map<String, Object>> maps = new ArrayList<>();
        if(discussPosts != null) {
            for (DiscussPost post : discussPosts) {
                String userId = post.getUserId();
                User user = userService.findUserById(Integer.valueOf(userId));
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", user);
                maps.add(map);

            }
        }
        /**
         * thyemleaf 根据键拿值
         */
        model.addAttribute("discussPost", maps);
        return "index";
    }

}
