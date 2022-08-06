package com.tencent.community;

import com.tencent.community.service.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommonTest {

    @Autowired
    DemoService demoService;
    @Test
    public void testTransaction(){
//        String s = demoService.demoTransaction();
        demoService.templateTransaction();
    }
}
