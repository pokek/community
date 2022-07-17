package com.tencent.community;

import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class UserTest {

    @Autowired
    UserMapper userMapper;

    @Test
    public void userSelect(){
        User user = userMapper.userById(149);
        System.out.println(user);
        user = userMapper.userByEmail("nowcoder149@sina.com");
        System.out.println(user);
        user = userMapper.userByName("niuke");
        System.out.println(user);

    }

    @Test
    public void userInsert() throws ParseException {
        User user = new User();
        user.setUserName("gag");
        user.setPassword("gaiwjgow");
        user.setSalt("gwg");
        user.setEmail("Gaghaoeg");
        user.setType(2);
        user.setStatus(1);
        user.setActivationCode("Gaegw");
        user.setHeaderUrl("gjaegw");
//        new Date() 格式自动对应数据库 yy-mm-dd
        user.setCreateTime(new Date());

        userMapper.insertUser(user);
    }

    @Test
    public void testUpdate(){
        userMapper.updateHeader(150, "gaegwa");
        userMapper.updatePassword(150, "jofwjg");
        userMapper.updateStatus(150, 2);
    }
}
