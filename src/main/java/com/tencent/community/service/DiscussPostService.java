package com.tencent.community.service;

import com.tencent.community.dao.PostMapper;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    public List<DiscussPost> discussPostByUserId(int userId, int row, int pageSize, int orderMode){

        return postMapper.discussPostByUserId(userId, row, pageSize, orderMode);
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
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(filterTool.filter(post.getTitle()));
        post.setContent(filterTool.filter(post.getContent()));
        // 插入数据库
        return postMapper.insertPost(post);
    }

    public DiscussPost findPostById(int id){
        return postMapper.selectPostById(id);
    }

    public int updatePostCommentCount(int id, int count){
        return postMapper.updatePostCommentCount(id, count);
    }

    public int updateType(int id, int type) {
        return postMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return postMapper.updateStatus(id, status);
    }


    public void updateScore(int postId, double score) {
        postMapper.updateScore(postId, score);
    }
}
