package com.tencent.community.service;

import com.tencent.community.dao.UserMapper;
import com.tencent.community.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User findUserById(int userId){
        return userMapper.userById(userId);
    }
}
