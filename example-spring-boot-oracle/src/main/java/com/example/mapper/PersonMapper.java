package com.example.mapper;

import com.example.entity.Person;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: zhouyuyang
 * @Date: 2020/8/28 11:05
 */
public interface PersonMapper {
    List<Person> selectAll();

    void insert(@Param("person") Person person);

    void update(@Param("person") Person person);

    void delete(Integer pid);

    Person findOne(Integer pid);

    /**
     * 分页查询
     * @param offset  第几页
     * @param limit   页数量
     * @return
     */
    List<Person> selectAllByPage(@Param("offset") int offset,@Param("limit") int limit);
}
