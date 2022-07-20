package com.tencent.community;

import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.CommunityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;

@SpringBootTest
public class ThreadLocalTest implements CommunityConstant {

    @Test
    public void test(){
        ThreadLocal threadLocal = new ThreadLocal();
        threadLocal.set("2");
        threadLocal.set("3");
        System.out.println(threadLocal.get());
        ThreadLocal threadLocal1 = new ThreadLocal();
        threadLocal1.set("4");
        System.out.println(threadLocal1.get());
    }

    @Test
    public void testTime(){
        System.out.println(System.currentTimeMillis());
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(new Date(System.currentTimeMillis() + LONG_EXPIRED_SECONDS * 1000));
        /*

            ! int类型是有符号位的，范围应该在2^31-1之间 不为2^32-1
         */
        System.out.println(System.currentTimeMillis() + LONG_EXPIRED_SECONDS * 1000L);
        String abc = null;

        /*
            false;
         */
        System.out.println("abc".equals(abc));
    }


}
