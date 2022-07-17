package com.tencent.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootTest
class CommunityApplicationTests implements ApplicationContextAware {

//	@Autowired
//	ApplicationContext applicationContext;

	ApplicationContext app;

	@Test
	public void contextLoads() {
		System.out.println(app);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.app = applicationContext;

	}
}
