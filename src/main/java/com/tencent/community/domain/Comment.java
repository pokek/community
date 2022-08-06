package com.tencent.community.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Comment {

    private Integer id;
    private Integer userId;
    /*
        表示评论类型，1 表示帖子评论   2 表示评论的评论 即回复评论
     */
    private Integer entityType;
    /*
        表示的为该类型下 评论的类型的id
        如 帖子类型 则是帖子的id
          回复类型  则是 帖子类型评论的id
     */
    private Integer entityId;
    /*
        表示的为回复评论 回复的用户id
        0 代表没有回复任何用户，
     */
    private Integer targetId = 0;
    private String content;
    /*
        表示评论的状态
     */
    private Integer status;
    private Date createTime;

}
