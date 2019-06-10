package com.zyy.dao;

import com.zyy.domain.Role;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述：RoleMapper
 *
 */
@org.apache.ibatis.annotations.Mapper
public interface RoleMapper extends Mapper<Role> {

    /**
     * 功能描述：通过用户名查询数据库中对应的角色信息
     * @param username
     * @return List<Role>
     **/
    @Select("select r.* from user u,user_role ur,role r\n" +
            "where u.uid = ur.uid and ur.role_id = r.rid\n" +
            "and u.username = #{username}")
    List<Role> findRoleByUser(String username);
}
