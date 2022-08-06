package com.tencent.community.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Message {

    private Integer id;
    private Integer fromId;
    private Integer toId;
    /*
        会话id
     */
    private String conversationId;
    private String content;
    /*
        0 表示未读
        1 表示已读
        2 表示删除
     */
    private Integer status;
    private Date createTime;
}
