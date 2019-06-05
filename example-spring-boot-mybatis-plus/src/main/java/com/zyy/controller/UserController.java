package com.zyy.controller;

import com.zyy.entity.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 14:33 2019/5/21
 */
@RestController
public class UserController {

    @RequestMapping(value = "/testList",method = RequestMethod.POST)
    public Object test(@RequestBody List<User> userList){
        return userList.stream().filter( user -> user.getUserName().equals("admin")).collect(Collectors.toList());
    }

    @RequestMapping(value = "/testMap",method = RequestMethod.POST)
    public Object testMap(@RequestBody Map<String,Object> maps){
        return maps.entrySet().stream().filter(entry -> entry.getKey().equals("admin")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @RequestMapping(value = "/testMapList",method = RequestMethod.POST)
    public Object testMapList(@RequestBody List<Map<String,Object>> mapList){
        return mapList.parallelStream().map(x-> x.entrySet().stream().filter(y -> y.getKey().equals("admin"))).collect(Collectors.toList());
    }
}
