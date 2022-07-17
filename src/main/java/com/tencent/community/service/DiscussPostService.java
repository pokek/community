package com.tencent.community.service;

import com.tencent.community.dao.PostMapper;
import com.tencent.community.domain.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    PostMapper postMapper;

    /**
     * 查询某页数据
     */
    public List<DiscussPost> discussPostByUserId(int userId, int row, int pageSize){

        return postMapper.discussPostByUserId(userId, row, pageSize);
    }

    /**
     * 查询总条数
     */
    public int findAllPost(int userId){
        return postMapper.discussionPostAll(userId);
    }
}
