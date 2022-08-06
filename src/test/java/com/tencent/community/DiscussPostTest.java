package com.tencent.community;

import com.tencent.community.dao.PostMapper;
import com.tencent.community.domain.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.ToIntFunction;

@SpringBootTest
public class DiscussPostTest {

    @Autowired
    PostMapper postMapper;

    @Test
    public void selectTest(){
        List<DiscussPost> discussPosts = postMapper.discussPostByUserId(0, 0, 10, 0);
        System.out.println(discussPosts);
        System.out.println(postMapper.discussionPostAll(0));
    }
}
