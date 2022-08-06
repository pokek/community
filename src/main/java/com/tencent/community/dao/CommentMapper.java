package com.tencent.community.dao;

import com.tencent.community.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentMapper {

    /*
        该mapper方法，根据参数类型不同 找出不同类型的评论 如 帖子评论 回复评论
     */

    List<Comment> selectCommentForPage(int entityId, int entityType, int offset, int limit);

    int findAllComment(int entityId, int entityType);

    int insertComment(Comment comment);

    Comment findCommentById(Integer entityId);


}
