package com.campus.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.agent.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);
}
