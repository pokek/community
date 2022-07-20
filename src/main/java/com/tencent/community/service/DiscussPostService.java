package com.tencent.community.service;

import com.tencent.community.dao.PostMapper;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    PostMapper postMapper;

    @Autowired
    SensitiveFilter filterTool;

    /*
      查询某页数据
     */
    public List<DiscussPost> discussPostByUserId(int userId, int row, int pageSize){

        return postMapper.discussPostByUserId(userId, row, pageSize);
    }

    /*
      查询总条数
     */
    public int findAllPost(int userId){
        return postMapper.discussionPostAll(userId);
    }


    public int addPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空值");
        }
        // 转义html格式文件防止页面崩坏
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        // 过滤敏感词
        post.setTitle(filterTool.filter(post.getTitle()));
        post.setContent(filterTool.filter(post.getContent()));
        // 插入数据库
        return postMapper.insertPost(post);
    }

}
