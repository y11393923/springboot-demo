package com.example.mapper;

import com.example.entity.Person;

import java.util.List;

/**
 * @Author: zhouyuyang
 * @Date: 2020/8/28 11:05
 */
public interface PersonMapper {
    List<Person> selectAll();
}
