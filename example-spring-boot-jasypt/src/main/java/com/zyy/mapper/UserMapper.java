package com.zyy.mapper;

import com.zyy.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhouyuyang_vendor
 */
@Mapper
public interface UserMapper {
    @Select("select * from user_info")
    @ResultType(UserInfo.class)
    List<UserInfo> queryAll();
}
