package com.zyy.service.impl;

import com.zyy.dao.UserDao;
import com.zyy.dao2.UserDao2;
import com.zyy.entity.User;
import com.zyy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:19 2019/5/29
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserDao2 userDao2;
    @Override
    public User addUser(User user) {
        return userDao.save(user);
    }

    @Override
    public List<User> findByUserName(String userName,Pageable pageable) {
        return userDao.findByUserName(userName,pageable);
    }

    @Override
    public int updateById(User user) {
        return userDao.updateById(user);
    }

    @Override
    public Page<User> findByPage(Pageable pageable) {
        return userDao.findAll(pageable);
    }

    @Override
    public List<User> findByIds(Integer[] ids) {
        return userDao.findByIds(ids);
    }

    @Override
    public List<User> findByAllDataBase() {
        List<User> userList = userDao.findAll();
        userList.addAll(userDao2.findAll());
        return userList;
    }
}
