package com.tencent.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LearnTest {

    @Test
    public void testStringTable(){
        String s1 = "a";
        String s2 = "b";
        String s3 = "a" + "b";
        String s4 = s1 + s2;
        String s5 = "ab";
        String s6 = s4.intern();
        System.out.println(s3 == s4);  // false
        System.out.println(s3 == s5);  // true
        System.out.println(s3 == s6);  // true
        // 问 System.out.println(s3 == s4); System.out.println(s3 == s5); System.out.println(s3 == s6); String x2 = new String("c") + new String("d"); String x1 = "cd"; x2.intern();
        // 问，如果调换了【最后两行代码】的位置呢，如果是jdk1.6呢 System.out.println(x1 == x2);

//        String x2 = new String("c") + new String("d");
//        String x1 = "cd";
//        String x3 = x2.intern();
//        System.out.println(x1 == x2); // false;
//        System.out.println(x1 == x3); // true;

        // 交换位置
        String x2 = new String("c") + new String("d");
        // intern() 1.8 是把字符串放入串池中 并且x2, x3都指向串池， 1.6 x3指向串池，x2指向copy后的内容
        String x3 = x2.intern();
        String x1 = "cd";
        System.out.println(x1 == x2); // true;
        System.out.println(x1 == x3); // true;

    }
}
