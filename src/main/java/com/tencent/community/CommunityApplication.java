package com.tencent.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.Deque;
import java.util.LinkedList;

@SpringBootApplication
public class CommunityApplication {
	// 解决redis，es netty处理器冲突  7.15.2版本底层代码已修复
//	@PostConstruct
//	public void init(){
//		System.setProperty("es.set.netty.runtime.available.processors", "false");
//	}
	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);

	}

}
