package com.tencent.community.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class LoginTicket {

    private Integer id;
    private Integer userId;
    private String ticket;
    /**
     * 0 有效， 1 无效
     */
    private Integer status;
    private Date expired;
}
