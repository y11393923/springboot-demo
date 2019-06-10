package com.zyy.service;

import com.zyy.dao.PermissionMapper;
import com.zyy.domain.Permission;
import com.zyy.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 功能描述：PermissionService
 *
 */
@Service
@Transactional
public class PermissionService {

    /**
     * 描述：permissionMapper对象
     */
    @Autowired
    private PermissionMapper permissionMapper;
    /**
     * 功能描述：findPermissionByUser
     * @param user
     * @return java.util.List<com.czxy.domain.Permission>
     **/
    public List<Permission> findPermissionByUser(User user) {
        return permissionMapper.findPermissionByUser(user.getUsername());
    }
}
