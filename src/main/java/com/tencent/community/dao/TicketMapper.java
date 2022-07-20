package com.tencent.community.dao;

import com.tencent.community.domain.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TicketMapper {

    public LoginTicket selectTicket(String ticket);

    public int insertTicket(LoginTicket loginTicket);

    public int updateTicket(String ticket, int status);
}
