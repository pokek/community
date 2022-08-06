package com.tencent.community.service;

import com.tencent.community.dao.PostMapper;
import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class DemoService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PostMapper postMapper;

    private Logger logger = LoggerFactory.getLogger(DemoService.class);

    /*
        可以用在 当一个事务需要很多步数据库操作时，而我们只需要控制其中一步数据库操作时的场景下。
     */
    @Autowired
    TransactionTemplate transactionTemplate;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String demoTransaction(){
        User user = new User();
        user.setUserName("xiaopingping");
        user.setPassword("wfosjodoajdgad");
        user.setSalt("dgoajg");
        user.setEmail("qq@123");
        user.setType(0);
        user.setStatus(1);
        user.setActivationCode("sdhgahsdg");
        user.setHeaderUrl("giwgwg");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussPost post = new DiscussPost();
        post.setUserId("220");
        post.setTitle("iwgwog");
        post.setContent("wfiogoew");
        post.setType(0);
        post.setStatus(0);
        post.setCreateTime(new Date());
        post.setCommentCount(20);
        post.setScore(10.0);
        postMapper.insertPost(post);
        System.out.println(1 / 0);
        return "ok";
    }

    public void templateTransaction(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(status -> {
            User user = new User();
            user.setUserName("xiaopingping");
            user.setPassword("wfosjodoajdgad");
            user.setSalt("dgoajg");
            user.setEmail("qq@123");
            user.setType(0);
            user.setStatus(1);
            user.setActivationCode("sdhgahsdg");
            user.setHeaderUrl("giwgwg");
            user.setCreateTime(new Date());
            userMapper.insertUser(user);

            DiscussPost post = new DiscussPost();
            post.setUserId("220");
            post.setTitle("iwgwog");
            post.setContent("wfiogoew");
            post.setType(0);
            post.setStatus(0);
            post.setCreateTime(new Date());
            post.setCommentCount(20);
            post.setScore(10.0);
            postMapper.insertPost(post);
            System.out.println(1 / 0);
            return "ok";

        });
    }


    // 让该方法在多线程环境下,被异步的调用.
    @Async
    public void execute1() {
        logger.debug("execute1");
    }

//    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2() {
        logger.debug("execute2");
    }


}
