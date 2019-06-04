package com.zyy.service.impl;

import com.zyy.entity.Menu;
import com.zyy.entity.User;
import com.zyy.entity.UserRole;
import com.zyy.enums.ResultCode;
import com.zyy.exception.CustomException;
import com.zyy.mapper.MenuMapper;
import com.zyy.mapper.UserMapper;
import com.zyy.mapper.UserRoleMapper;
import com.zyy.mapper2.UserMapper2;
import com.zyy.response.ResponseResult;
import com.zyy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 17:28 2019/5/27
 */
@Service
public class UserServiceImpl implements UserService,UserDetailsService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserMapper2 userMapper2;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        List<User> userList = userMapper.findAll();
        userList.addAll(userMapper2.findAll());
        return userList;
    }

    /**
     * 多数据源需要制定transactionManager  不然事务不回滚
     */
    @Override
    @Transactional(transactionManager = "transactionManagerOne", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseResult register(String userName, String password) throws CustomException {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            throw new CustomException("用户名或密码不能为空");
        }
        User user = new User();
        user.setUsername(userName);
        user.setNickname(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateDate(new Date());
        int result = userMapper.addUser(user);
        if (result <= 0) {
            throw new CustomException(ResultCode.FAIL.getMessage());
        }
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2);
        userRole.setCreateTime(new Date());
        result = userRoleMapper.addUserRole(userRole);
        if (result <= 0) {
            throw new CustomException(ResultCode.FAIL.getMessage());
        }
        return ResponseResult.success();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(username);
        if (null == user){
            throw new UsernameNotFoundException(ResultCode.ERROR_ACCOUNT_NAME_OR_PASSWORD.getMessage());
        }
        return user;
    }
}
