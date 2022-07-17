package com.tencent.community.dao;

import com.tencent.community.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {

    User userById(int id);

    User userByName(String name);

    User userByEmail(String email);

    int insertUser(User user);

    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updateHeader(@Param("id") int id, @Param("header") String header);

    int updatePassword(@Param("id") int id, @Param("password") String password);
}
