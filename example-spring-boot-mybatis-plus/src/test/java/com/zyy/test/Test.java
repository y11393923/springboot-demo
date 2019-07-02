package com.zyy.test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyy.dao.UserMapper;
import com.zyy.entity.User;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:31 2019/5/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
    @Autowired
    private UserMapper userMapper;

    @org.junit.Test
    public void testSelect(){
        Page<User> page = new Page<>(2,2);
        List<User> userList = userMapper.selectAll(page);
        page.setRecords(userList);
        System.out.println(userList.size());
        System.out.println(page.getTotal());
        System.out.println(page.getPages());
        for (User user:userList) {
            System.out.println(user.toString());
        }
    }
    @org.junit.Test
    public void testUpdate(){
        User user = userMapper.selectById(1);
        System.out.println(user.toString());
        user.setUserName("王五");
        userMapper.updateById(user);
        System.out.println(user.toString());
    }
}
