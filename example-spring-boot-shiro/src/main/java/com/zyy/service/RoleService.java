package com.zyy.service;

import com.zyy.dao.RoleMapper;
import com.zyy.domain.Role;
import com.zyy.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 功能描述：RoleService
 *
 */
@Service
@Transactional
public class RoleService {

    /**
     * 描述：roleMapper对象
     */
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 功能描述：findRoleByUser
     * @param user
     * @return java.util.List<com.czxy.domain.Role>
     **/
    public List<Role> findRoleByUser(User user) {

        return roleMapper.findRoleByUser(user.getUsername());
    }
}
