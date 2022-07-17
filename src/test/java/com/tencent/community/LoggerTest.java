package com.tencent.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoggerTest {

    /**
     * 记录自己写的程序运行过程中的相关信息，以日志形式进行输出
     * info 记录定时或线程池信息，根据自己程序输出额信息进行级别类
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);


    @Test
    public void testLog(){
        System.out.println(logger.getName());
        logger.trace("trace log");
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warning log");
        logger.error("error log");
    }
}
