package com.zyy.mapper;

import com.zyy.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhouyuyang_vendor
 */
@Mapper
public interface StudentMapper{

    int insert(@Param("student") Student student);
}
