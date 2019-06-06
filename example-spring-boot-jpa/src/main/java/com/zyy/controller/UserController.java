package com.zyy.controller;

import com.zyy.entity.User;
import com.zyy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:23 2019/5/29
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/test")
    public Object test(){
        User user1 =new User();
        user1.setUserName("zhangsan");
        user1.setPassword("123456");
        User user2 =new User();
        user2.setUserName("lisi");
        user2.setPassword("123456");
        userService.addUser(user1);
        User user = userService.addUser(user2);

        user.setPassword("abcde");
        int result = userService.updateById(user);
        System.out.println(result);

        List<User> userList = userService.findByUserName("lisi",PageRequest.of(0,1));
        userList.forEach(item ->{
            System.out.println(item.toString());
        });

        userList=userService.findByIds(new Integer[]{1,2});
        userList.forEach(item ->{
            System.out.println(item.toString());
        });

        //PageRequest.of 页数从0开始
        PageRequest pageable= PageRequest.of(0,2);
        Page<User> userPage = userService.findByPage(pageable);
        System.out.println("总页数:"+userPage.getTotalPages());
        System.out.println("总记录数:"+userPage.getTotalElements());
        System.out.println("当前页数:"+(userPage.getNumber()+1));
        System.out.println("当前页记录数:"+userPage.getNumberOfElements());
        System.out.println("每页记录数:"+userPage.getSize());
        return userPage.getContent();
    }

    @RequestMapping("/test2")
    public Object test2(){
        return userService.findByAllDataBase();
    }
}
