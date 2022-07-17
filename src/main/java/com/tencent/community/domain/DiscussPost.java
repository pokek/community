package com.tencent.community.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class DiscussPost {

    private Integer id;
    private String userId;
    private String title;
    private String content;
    /**
     * '0-普通; 1-置顶;'
     */
    private Integer type;
    /**
     * '0-正常; 1-精华; 2-拉黑;'
     */
    private Integer status;
    private Date createTime;
    private Integer commentCount;
    private Double score;
}
