package com.tencent.community.controller;

import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.Page;
import com.tencent.community.domain.User;
import com.tencent.community.service.DiscussPostService;
import com.tencent.community.service.LikeService;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class HomeAgeController implements CommunityConstant {

    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root(){
        return "forward:/index";
    }

    /*
           分页的每一页为一个超链接
     */

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String homeAge(Model model, Page page, @RequestParam(required = false, defaultValue = "0") int orderMode){
        page.setRows(discussPostService.findAllPost(0));
        page.setPath("/index");
        List<DiscussPost> discussPosts = discussPostService.discussPostByUserId(0, page.getStart(), page.getLimit(), orderMode);
        List<Map<String, Object>> maps = new ArrayList<>();
        if(discussPosts != null) {
            for (DiscussPost post : discussPosts) {
                String userId = post.getUserId();
                User user = userService.findUserById(Integer.valueOf(userId));
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);

                /*

                    你妹的，自己在post表中加了一条user表中没有的userid数据，导致
                    动态页面直接 ${content.user.useName}出现user为空的状况......
                    手贱加的数据一定要及时删除.....
                    不删数据的情况下 在动态页面中可以用 ${content.user.userName != null ?
                    content.user.userName : ''}来解决
                 */
                map.put("user", user);
                // 加入帖子点赞数量
                map.put("count", likeService.likeCount(post.getId(), ENTITY_TYPE_COMMENT));
                maps.add(map);

            }
        }
        /**
         * thyemleaf 根据键拿值
         */
        model.addAttribute("discussPost", maps);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }

}
