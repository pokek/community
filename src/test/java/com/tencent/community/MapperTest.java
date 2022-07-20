package com.tencent.community;

import com.tencent.community.dao.TicketMapper;
import com.tencent.community.domain.LoginTicket;
import com.tencent.community.util.CommunityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MapperTest {

    @Autowired
    TicketMapper ticketMapper;

    @Test
    public void testInsert(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(2);
        loginTicket.setTicket(CommunityUtils.getUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());

        ticketMapper.insertTicket(loginTicket);
    }

    @Test
    public void updateAndSelectTest(){
        LoginTicket loginTicket = ticketMapper.selectTicket("a966b41903c24b20b4df31e30046eca7");
        System.out.println(loginTicket);
        ticketMapper.updateTicket("a966b41903c24b20b4df31e30046eca7", 1);
    }
}
