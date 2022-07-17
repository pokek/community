package com.tencent.community.dao;

import com.tencent.community.domain.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PostMapper {

    /**
     * 动态sql
     * 首页分页显示，个人主页分页显示
     */
    public List<DiscussPost> discussPostByUserId(@Param("userid") int userId, @Param("row") int row, @Param("pageSize") int pageSize);

    /**
     * 计算页码 首页，个人详情页
     */
    public int discussionPostAll(@Param("userid") int userId);

}
