package com.tencent.community.service;

import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.User;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.CommunityUtils;
import com.tencent.community.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.CustomSQLExceptionTranslatorRegistrar;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    UserMapper userMapper;

    @Autowired
    MailClient mailsender;

    @Autowired
    TemplateEngine tEngine;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Value("${community-domain}")
    String domain;

    public User findUserById(int userId){
        return userMapper.userById(userId);
    }

    public Map<String, String> register(User user){
        Map<String, String> maps = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("非法的参数");
        }
        if(user.getUserName() == null){
            maps.put("usernameMes", "用户名不能为空!");
        }
        if(user.getPassword() == null){
            maps.put("passwordMes", "密码不能为空!");
        }
        if(user.getEmail() == null){
            maps.put("emailMes", "邮箱不能为空!");
        }
        if(userMapper.userByName(user.getUserName()) != null) {
            maps.put("usernameMes", "用户名重复!");
        }
        if(userMapper.userByEmail(user.getEmail()) != null){
            maps.put("emailMes", "邮箱重复!");
        }

        if(maps.isEmpty()){
            user.setSalt(CommunityUtils.getUUID().substring(0, 5));
            user.setPassword(user.getPassword() + user.getSalt());
            user.setType(0);
            user.setStatus(0);
            user.setActivationCode(CommunityUtils.getUUID());
            user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
            user.setCreateTime(new Date());

            userMapper.insertUser(user);

            String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();

            Context context = new Context();
            context.setVariable("url", url);
            context.setVariable("email", user.getEmail());
            String ans = tEngine.process("mail/activation", context);
            mailsender.sendMail(user.getEmail(), "激活邮件", ans);
            return null;
        }
        return maps;
    }

    public int activation(int userId, String activationCode){
        User user = userMapper.userById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(activationCode)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }


}
