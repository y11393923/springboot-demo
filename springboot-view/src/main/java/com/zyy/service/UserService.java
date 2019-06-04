package com.zyy.service;

import com.zyy.entity.User;
import com.zyy.exception.CustomException;
import com.zyy.response.ResponseResult;

import java.util.List;

public interface UserService {
    List<User> findAll();

    ResponseResult register(String userName, String password) throws CustomException;
}
