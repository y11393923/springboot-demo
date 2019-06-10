package com.zyy.dao;

import com.zyy.domain.User;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * 功能描述：User的通用Mapper
 *
 * @author https://blog.csdn.net/chen_2890
 * @date 2018/12/5 19:38:25
 */
@org.apache.ibatis.annotations.Mapper
public interface UserMapper extends Mapper<User> {

    /**
     * 功能描述：通过用户名查询用户信息
     * @param username
     * @return User
     **/
    @Select("SELECT * FROM user where username=#{username}")
    User findUserByUsername(String username);

}
