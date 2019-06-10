package com.zyy.dao;

import com.zyy.domain.Permission;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述：PermissionMapper
 */
@org.apache.ibatis.annotations.Mapper
public interface PermissionMapper extends Mapper<Permission> {

    /**
     * 功能描述：通过用户名查询数据库中对应的权限信息
     * @param username
     * @return List<Permission>
     **/
    @Select("select p.* from permission p,user u,user_role ur,role_permission rp\n" +
            "where u.uid = ur.uid and ur.role_id = rp.role_id and rp.permission_id = p.pid\n" +
            "and u.username = #{username}")
    List<Permission> findPermissionByUser(String username);

}
