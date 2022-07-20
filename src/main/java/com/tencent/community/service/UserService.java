package com.tencent.community.service;

import com.tencent.community.dao.TicketMapper;
import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.LoginTicket;
import com.tencent.community.domain.User;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.CommunityUtils;
import com.tencent.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
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
    TicketMapper ticketMapper;

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
            // 使用MD5进行密码加密
            user.setPassword(CommunityUtils.md5(user.getPassword() + user.getSalt()));
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

    public Map<String, String> login(String username, String password, int expiresSeconds){
        Map<String, String> maps = new HashMap<>();
        if(StringUtils.isBlank(username)){
            maps.put("usermes", "账号不能为空");
            return maps;
        }
        if(StringUtils.isBlank(password)){
            maps.put("passwordmes", "密码不能为空");
            return maps;
        }
        User user = userMapper.userByName(username);
        if(user == null){
            maps.put("usermes", "该账号不存在");
            return maps;
        }
        if(!user.getPassword().equals(CommunityUtils.md5(password + user.getSalt()))){
            maps.put("passwordmes", "密码不正确");
            return maps;
        }
        if(user.getStatus() == 0){
            maps.put("usermes", "该账户未激活");
            return maps;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtils.getUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiresSeconds * 1000L));
        ticketMapper.insertTicket(loginTicket);
        maps.put("ticket", loginTicket.getTicket());
        return maps;
    }

    public LoginTicket findTicket(String ticket){
        LoginTicket loginTicket = ticketMapper.selectTicket(ticket);
        return loginTicket;
    }

    public void updateTicketStaus(String ticket){
        ticketMapper.updateTicket(ticket, 1);
    }


    public int updateUserHeaderUrl(int id, String headerUrl){
        return userMapper.updateHeader(id, headerUrl);
    }

    public int updateUserPassword(User user, String password){
        // 生成加密密码
        String salt = user.getSalt();

        /*
            ! 加密方式的字符串排列得一致........
         */
        password = CommunityUtils.md5(password + salt);
        // 更新数据库密码
        return userMapper.updatePassword(user.getId(), password);
    }


}
