package com.tencent.community.service;

import com.tencent.community.dao.CommentMapper;
import com.tencent.community.domain.Comment;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    CommentMapper commentMapper;


    @Autowired
    SensitiveFilter sensitiveFilter;

    @Autowired
    DiscussPostService postService;

    public List<Comment> findComments(int entityId, int entityType, int offset, int limit){
        return commentMapper.selectCommentForPage(entityId, entityType, offset, limit);
    }

    public int findAllComments(int entityId, int entityType){
        return commentMapper.findAllComment(entityId, entityType);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数类型不合法");
        }
        // 过滤文本和敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        // 插入评论表
        int ans = commentMapper.insertComment(comment);
        // 更新帖子表
        if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            int count = commentMapper.findAllComment(comment.getEntityId(), ENTITY_TYPE_COMMENT);
            postService.updatePostCommentCount(comment.getEntityId(), count);
        }
        return ans;
    }

    public Comment findCommentById(Integer entityId) {
        return commentMapper.findCommentById(entityId);
    }
}
