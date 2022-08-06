package com.tencent.community.controller;

import com.tencent.community.annotation.LoginRequired;
import com.tencent.community.domain.*;
import com.tencent.community.event.EventProducer;
import com.tencent.community.service.CommentService;
import com.tencent.community.service.DiscussPostService;
import com.tencent.community.service.LikeService;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.HostHolder;
import com.tencent.community.util.JsonUtils;
import com.tencent.community.util.LikeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@RequestMapping(value = "/post")
public class DiscussPostController implements CommunityConstant {


    @Autowired
    HostHolder hd;

    @Autowired
    DiscussPostService postService;

    @Autowired
    UserService us;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    RedisTemplate redisTemplate;


    @LoginRequired
    @RequestMapping(value = "/add", method = RequestMethod.POST)
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

        // 发布帖子事件
        Event event = new Event();
        event.setTopic(TOPIC_PUBLISH)
                .setUserId(hd.get().getId())
                .setEntityId(post.getId())
                .setEntityType(ENTITY_TYPE_COMMENT);
        eventProducer.sendEvent(event);

        // 计算帖子分数
        String redisKey = LikeUtils.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        // 返回json响应数据
        return JsonUtils.getJsonString(200, "帖子发布成功");
    }

    /*

        前往帖子详情
        分页的时候请求样式: http://localhost:85/community/post/detail/{id}?&current=...
        所以可以被处理到

        查找的数据在数据库中已经排好了，接收的时候使用list接收，还是有序的
        数据库没找到数据会返回0
        List<Comment> replyList = commentService.findComments(cm.getId(), ENTITY_TYPE_REPLY, 0, Integer.MAX_VALUE);  replyList.size() = 0;

        Page 会被生成加入model
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public String dicussDetail(@PathVariable("id") int id, Model model, Page page){
        DiscussPost post = postService.findPostById(id);
        User user = us.findUserById(Integer.valueOf(post.getUserId()));
        model.addAttribute("post", post);
        model.addAttribute("postuser", user);

        // 设置分页数据
        page.setPath("/post/detail/" + id);
        page.setLimit(5);
        page.setRows(post.getCommentCount());

        // 查找帖子评论  时间降序
        List<Comment> commentList = commentService.findComments(id, ENTITY_TYPE_COMMENT, page.getStart(), page.getLimit());
        // 帖子详情页面显示体
        List<Map<String, Object>> cvos = new ArrayList<>();
        if(commentList != null || commentList.size() != 0){
            for(Comment cm : commentList){
                // 查询帖子评论的评论用户
                User cUser = us.findUserById(cm.getUserId());
                Map<String, Object> cvo = new HashMap<>();
                cvo.put("comment", cm);
                cvo.put("cuser", cUser);
                // 加入帖子评论的点赞数和点赞状态
                cvo.put("status", likeService.likeStatus(hd.get().getId(), cm.getId(), ENTITY_TYPE_REPLY));
                cvo.put("count", likeService.likeCount(cm.getId(), ENTITY_TYPE_REPLY));
                // 查找该帖子评论的回复评论   时间降序
                List<Comment> replyList = commentService.findComments(cm.getId(), ENTITY_TYPE_REPLY, 0, Integer.MAX_VALUE);
                // 回复评论的显示体
                List<Map<String, Object>> rvos = new ArrayList<>();
                if(replyList != null || replyList.size() != 0){
                    for(Comment reply : replyList){
                        // 回复评论的用户
                        User rUser = us.findUserById(reply.getUserId());
                        Map<String, Object> rvo = new HashMap<>();
                        rvo.put("reply", reply);
                        rvo.put("ruser", rUser);
                        rvo.put("status", likeService.likeStatus(hd.get().getId(), reply.getId(), ENTITY_TYPE_REPLY));
                        rvo.put("count", likeService.likeCount(reply.getId(), ENTITY_TYPE_REPLY));
                        // 加入该回复评论指向的评论底下的某个用户
                        User target = reply.getTargetId() == 0 ? null : us.findUserById(reply.getTargetId());
                        rvo.put("target", target);
                        rvos.add(rvo);
                    }
                }
                cvo.put("rvos", rvos);
                // 加入回复该帖子评论的数量
                int cCounts = commentService.findAllComments(cm.getId(), ENTITY_TYPE_REPLY);
                cvo.put("ccounts", cCounts);
                cvos.add(cvo);
            }
        }
        // 加入帖子点赞数和点赞状态
        model.addAttribute("count", likeService.likeCount(id, ENTITY_TYPE_COMMENT));
        model.addAttribute("status", likeService.likeStatus(hd.get().getId(), id, ENTITY_TYPE_COMMENT));
        model.addAttribute("cvos", cvos);

        return "/site/discuss-detail";
    }


    // 置顶
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        postService.updateType(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hd.get().getId())
                .setEntityType(ENTITY_TYPE_COMMENT)
                .setEntityId(id);
        eventProducer.sendEvent(event);

        return JsonUtils.getJsonString(0);
    }

    // 加精
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        postService.updateStatus(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hd.get().getId())
                .setEntityType(ENTITY_TYPE_COMMENT)
                .setEntityId(id);
        eventProducer.sendEvent(event);

        // 计算帖子分数
        String redisKey = LikeUtils.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return JsonUtils.getJsonString(0);
    }

    // 删除
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        postService.updateStatus(id, 2);

        // 触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hd.get().getId())
                .setEntityType(ENTITY_TYPE_COMMENT)
                .setEntityId(id);
        eventProducer.sendEvent(event);

        return JsonUtils.getJsonString(0);
    }
}
