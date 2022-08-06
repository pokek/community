package com.tencent.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Hashtable;
import java.util.Scanner;

public class ScannerTest {

    public static void main(String[] args) {
//        Scanner in = new Scanner(System.in);
//        // hasNext 和 hasNextLine在输入之前是会堵塞滴，且hasNex遇到结束符(空格和换行)就读下一个控制台输入
//        // 而hasNextLine 一行之中就算有空格结束符，也读取一行
//        // hasNextInt 遇到输入不为数字类型的字符，直接结束
//        while(in.hasNextInt()){
//            System.out.println(in.next());
//        }
        Scanner in = new Scanner(System.in);
        // hasNext遇到非数字字符直接结束程序，而hasNextLine和hasNext一直等在下次输入
        // 用的时候 最好配合使用 hasNext  next ;  hasNextLine nextLine ; hasNextInt nextInt;
        while (in.hasNextLine()) {
            int i = in.nextInt();
            int res = 0;
            for (int j = 0; j < i; j++) {
                res += in.nextInt();
            }
            System.out.println(res);
        }
    }
}
