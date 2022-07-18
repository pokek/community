package com.tencent.community;

import com.tencent.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class TestMail {

    @Autowired
    MailClient mailSend;

    @Autowired
    TemplateEngine tEngine;



    @Test
    public void testSend(){
        mailSend.sendMail("2655835308@qq.com", "test", "测试内容");

    }

    @Test
    public void testSendHtml(){
        Context context = new Context();
        context.setVariable("username", "小明");
        String res = tEngine.process("/email/mail", context);
        mailSend.sendMail("2655835308@qq.com", "html", res);
    }
}
