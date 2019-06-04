package com.zyy.dao;

import com.zyy.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:       加上spring-boot-starter-data-rest依赖 生成restful接口
 *      查询接口  GET   http://localhost:8082/users?page=0&size=10&sort=id,desc     http://localhost:8082/users/1
 *      新增接口  POST  http://localhost:8082/users        params:{"password":"test","userName":"admin"}
 *      修改接口  PUT   http://localhost:8082/users        params:{"password":"test2","userName":"admin2"}
 *      删除接口  DELETE http://localhost:8082/1
 *
 *      RepositoryRestResource注解 path配置接口路径  collectionResourceRel标识key名称  exported 表示是否开启rest默认为true
 * @Date: Created in 10:08 2019/5/29
 */
@RepositoryRestResource(exported = true)
public interface UserDao extends JpaRepository<User,Integer>{

    List<User> findByUserName(String userName, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update t_user set user_name=:#{#user.userName},password=:#{#user.password} where id=:#{#user.id}",nativeQuery = true)
    int updateById(@Param("user")User user);

    @Query(value = "select * from t_user where id in (:ids)",nativeQuery = true)
    List<User> findByIds(@Param("ids")Integer[] ids);

    @RestResource(path = "userName",rel = "userName")
    List<User> findByUserName(@Param("userName") String userName);
}
