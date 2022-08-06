package com.tencent.community.service;

import com.tencent.community.dao.TicketMapper;
import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.LoginTicket;
import com.tencent.community.domain.User;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.CommunityUtils;
import com.tencent.community.util.LikeUtils;
import com.tencent.community.util.MailClient;
import io.lettuce.core.api.push.PushListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.support.CustomSQLExceptionTranslatorRegistrar;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    UserMapper userMapper;

//    @Autowired
//    TicketMapper ticketMapper;

    @Autowired
    MailClient mailsender;

    @Autowired
    TemplateEngine tEngine;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Value("${community-domain}")
    String domain;

    @Autowired
    RedisTemplate redisTemplate;

    public User findUserById(int userId){

//        return userMapper.userById(userId);
        User u = checkCache(userId);
        if(u == null){
            u = initCache(userId);
        }
        return u;
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

//        ticketMapper.insertTicket(loginTicket);
        // 存入redis中
        String ticketKey = LikeUtils.getLoginTicketKey(loginTicket.getTicket());
        // 对象通过配置的指定序列化方式 序列成json字符串
        // ticket和验证码都是直接存入redis中，当数据更新的时候也无后台数据库更新，不是作为缓存使用
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
        maps.put("ticket", loginTicket.getTicket());
        return maps;
    }

    public LoginTicket findTicket(String ticket){
//        LoginTicket loginTicket = ticketMapper.selectTicket(ticket);
        String ticketKey = LikeUtils.getLoginTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
    }

    public void updateTicketStaus(String ticket){
        String ticketKey = LikeUtils.getLoginTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }


    public int updateUserHeaderUrl(int id, String headerUrl){
        // 数据提交事务可能会未提交，导致回滚  所以为了避免回滚失败但缓存已删除的问题，先进行更新数据库，在删除缓存
        int count = userMapper.updateHeader(id, headerUrl);
        deleteCache(id);
        return count;
    }

    public int updateUserPassword(User user, String password){
        // 生成加密密码
        String salt = user.getSalt();

        /*
            ! 加密方式的字符串排列得一致........
         */
        password = CommunityUtils.md5(password + salt);
        // 更新数据库密码
        int count = userMapper.updatePassword(user.getId(), password);
        deleteCache(user.getId());
        return count;
    }

    public Integer findUserId(String username){
        return userMapper.findUserId(username);
    }


    // 把用户信息作为缓存，缓存到redis库中
    // 分层进行编程
    // 缓存 三步骤  查询缓存  初始化缓存   删除缓存
    private User checkCache(int userId){
        String userKey = LikeUtils.getUserKey(userId);
        User u = (User) redisTemplate.opsForValue().get(userKey);
        return u;
    }

    private User initCache(int userId){
        String userKey = LikeUtils.getUserKey(userId);
        User u = userMapper.userById(userId);
        redisTemplate.opsForValue().set(userKey, u, 3600, TimeUnit.SECONDS);
        return u;
    }

    private void deleteCache(int userId){
        String userKey = LikeUtils.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Integer userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch ((user.getType().intValue())) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}


