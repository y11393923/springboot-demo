package com.zyy.service;

import com.zyy.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> findByUserName(String userName,Pageable pageable);

    int updateById(User user);

    Page<User> findByPage(Pageable pageable);

    List<User> findByIds(Integer[] ids);

    List<User> findByAllDataBase();
}
