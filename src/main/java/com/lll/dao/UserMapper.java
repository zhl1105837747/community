package com.lll.dao;

import com.lll.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 根据id查询用户
    User selectById(int id);

    //根据姓名查找用户
    User selectByName(String username);

    //根据邮箱查找用户
    User selectByEmail(String email);

    //添加用户
    int insertUser(User user);

    //根据id修改用户的status
    int updateStatus(int id, int status);

    //根据id修改用户的headerUrl
    int updateHeader(int id, String headerUrl);

    //根据id修改用户的密码
    int updatePassword(int id, String password);
}
