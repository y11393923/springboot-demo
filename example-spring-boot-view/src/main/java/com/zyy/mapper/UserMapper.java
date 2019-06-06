package com.zyy.mapper;

import com.zyy.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> findAll();

    User loadUserByUsername(@Param("username") String username);

    int addUser(User user);
}
