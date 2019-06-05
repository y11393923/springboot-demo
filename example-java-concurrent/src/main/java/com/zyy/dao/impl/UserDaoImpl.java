package com.zyy.dao.impl;

import com.zyy.dao.UserDao;
import com.zyy.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 11:55 2019/4/30
 */
@Repository
public class UserDaoImpl implements UserDao {
    @Override
    public void save(User user) {
        System.out.println("保存用户信息"+user.toString());
    }
}
