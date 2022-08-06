package com.tencent.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.tencent.community.domain.Message;
import com.tencent.community.domain.Page;
import com.tencent.community.domain.User;
import com.tencent.community.service.MessageService;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.HostHolder;
import com.tencent.community.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
@RequestMapping(value = "/message")
public class MessageController implements CommunityConstant {

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService us;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String messageList(Model model, Page page){
        // 获取请求线程绑定的用户
        User user = hostHolder.get();
        // 设置分页属性
        page.setPath("/message/list");
        page.setRows(messageService.countSession(user.getId()));
        // 查询用户通知信息  分页
        List<Message> messageList = messageService.findSession(user.getId(), page.getStart(), page.getLimit());
        // 页面显示 通知体
        List<Map<String, Object>> avo =  new ArrayList<>();
        for(Message message : messageList){
            Map<String, Object> map = new HashMap<>();
            int targetId = user.getId().equals(message.getFromId()) ? message.getToId() : message.getFromId();
            User targetUser = us.findUserById(targetId);
            map.put("message", message);
            map.put("targetuser", targetUser);
            map.put("scount", messageService.findCountOnSessionMessage(message.getConversationId()));
            map.put("unread", messageService.findUnreadMessage(user.getId(), message.getConversationId()));
            avo.add(map);
        }
        model.addAttribute("avo", avo);
        int allUnread = messageService.findUnreadMessage(user.getId(), null);
        model.addAttribute("allunread", allUnread);
        int noticeUnreadCount = messageService.findTopicMessageUnread(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }


    @RequestMapping(value = "/detail/{seesionid}", method = RequestMethod.GET)
    public String seesionDetail(Model model, Page page, @PathVariable("seesionid") String sessionId){
        // 设置分页数据
        page.setPath("/message/detail/" + sessionId);
        page.setRows(messageService.findCountOnSessionMessage(sessionId));
        page.setLimit(5);

        User user = hostHolder.get();

        // 查找会话数据
        List<Message> messageList = new ArrayList<>();
        messageList = messageService.findOnSessionMessage(sessionId, page.getStart(), page.getLimit());
        List<Map<String, Object>> vo = new ArrayList<>();
        // 记录已读信息id
        List<Integer> ids = new ArrayList<>();
        for(Message message : messageList){
            Map<String, Object> map = new HashMap<>();
            map.put("message", message);
            User fromUser = us.findUserById(message.getFromId());
            map.put("fromuser", fromUser);
            if(!message.getFromId().equals(user.getId()) && message.getStatus().equals(0)){
                ids.add(message.getId());
            }
            vo.add(map);
        }
        String[] strings = sessionId.split("_");
        for(String s : strings){
            int tid = Integer.parseInt(s);
            if(tid != user.getId()){
                model.addAttribute("tuser", us.findUserById(tid));
            }
        }
        model.addAttribute("vo", vo);

        // 设置已读
        if(!ids.isEmpty()){
            messageService.updateMessage(ids, 1);
        }
        return "/site/letter-detail";
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String username, String content){
        if(StringUtils.isBlank(username) || StringUtils.isBlank(content)){
            return JsonUtils.getJsonString(404, "用户名或内容不能为空");
        }

        Integer userId = us.findUserId(username);
        if(userId == null){
            return JsonUtils.getJsonString(404, "您好，用户不存在，请重新输入");
        }
        User user = hostHolder.get();
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(userId);
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);
        String sessionId = user.getId() > userId ? userId + "_" + user.getId() : user.getId() + "_" + userId;
        message.setConversationId(sessionId);

        // 插入消息表中
        messageService.addMessage(message);
        return JsonUtils.getJsonString(0);
    }

    @RequestMapping(value = "/notice", method = RequestMethod.GET)
    public String getNoticePage(Model model){
        User user = hostHolder.get();

        // 查询评论类通知
        Message message = messageService.findNewMessage(user.getId(), TOPIC_COMMENT);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            // 系统通知 content中存的是发起人的userid
            messageVO.put("user", us.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findTopicMessageCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);

            int unread = messageService.findTopicMessageUnread(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", unread);
            model.addAttribute("commentNotice", messageVO);
        }


        // 查询点赞类通知
        message = messageService.findNewMessage(user.getId(), TOPIC_LIKE);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", us.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findTopicMessageCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);

            int unread = messageService.findTopicMessageUnread(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unread);
            model.addAttribute("likeNotice", messageVO);
        }


        // 查询关注类通知
        message = messageService.findNewMessage(user.getId(), TOPIC_FOLLOW);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", us.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findTopicMessageCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);

            int unread = messageService.findTopicMessageUnread(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unread);
            model.addAttribute("followNotice", messageVO);
        }


        // 查询未读消息数量
        int letterUnreadCount = messageService.findUnreadMessage(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findTopicMessageUnread(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";



    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.get();

        page.setLimit(5);
        page.setPath("/message/notice/detail/" + topic);
        page.setRows(messageService.findTopicMessageCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getStart(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", us.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知作者
                map.put("fromUser", us.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.updateMessage(ids, 1);
        }

        return "/site/notice-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.get().getId().equals(message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }


}
