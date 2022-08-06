package com.tencent.community;

import com.tencent.community.dao.*;
import com.tencent.community.domain.Comment;
import com.tencent.community.domain.LoginTicket;
import com.tencent.community.domain.Message;
import com.tencent.community.util.CommunityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    TicketMapper ticketMapper;

    @Autowired
    PostMapper postMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserMapper userMapper;

    @Test
    public void testInsert(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(2);
        loginTicket.setTicket(CommunityUtils.getUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());

        ticketMapper.insertTicket(loginTicket);
    }

    @Test
    public void updateAndSelectTest(){
        LoginTicket loginTicket = ticketMapper.selectTicket("a966b41903c24b20b4df31e30046eca7");
        System.out.println(loginTicket);
        ticketMapper.updateTicket("a966b41903c24b20b4df31e30046eca7", 1);
    }

    @Test
    public void testSelectPost(){
        System.out.println(postMapper.selectPostById(109));
    }

    @Test
    public void testCommentMapper(){
        List<Comment> comments = commentMapper.selectCommentForPage(228, 1, 0, 10);
        for(Comment comment : comments){
            System.out.println(comment.getCreateTime());
        }
    }

    @Test
    public void testCommentAll(){
        int allComment = commentMapper.findAllComment(228, 1);
        System.out.println(allComment);
    }

    @Test
    public void testInsertComment(){
        Comment comment = new Comment();
        comment.setContent("wgwgwe");
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setEntityId(200);
        comment.setUserId(102);
        comment.setTargetId(200);
        commentMapper.insertComment(comment);
    }

    @Test
    public void testPostUpdate(){
        postMapper.updatePostCommentCount(285, 20);
    }


    @Test
    public void testMessage(){
//        List<Message> messages = messageMapper.selectMessageByUserId(111, 0, Integer.MAX_VALUE);
//        for(Message message : messages){
//            System.out.println(message);
//        }
//        System.out.println(messageMapper.selectConversationCount(111));
//        List<Message> messages = messageMapper.selectMessageByConversationId("111_112", 0, Integer.MAX_VALUE);
//        for(Message message : messages){
//            System.out.println(message);
//        }

//        Message message = new Message();
//        message.setConversationId("gwgw");
//        messageMapper.insertMessage(message);
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(354);
        messageMapper.updateMessage(ids, 1);


//        System.out.println(messageMapper.selectMessageUnread(131, "111_131"));

//        System.out.println(messageMapper.selectMessageCountByConversationId("111_112"));
    }

    @Test
    public void testUserMapper(){
        System.out.println(userMapper.findUserId("owgag"));
    }

}
