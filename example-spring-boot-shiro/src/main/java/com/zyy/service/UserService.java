package com.zyy.service;

import com.zyy.dao.UserMapper;
import com.zyy.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 功能描述：UserService
 *
 */
@Service
@Transactional
public class UserService {

    /**
     * 描述：userMapper对象
     */
    @Autowired
    private UserMapper userMapper;
    /**
     * 功能描述：findUserByUsername
     * @param username
     * @return com.czxy.domain.User
     **/
    public User findUserByUsername(String username) {

        return userMapper.findUserByUsername(username);
    }
}
