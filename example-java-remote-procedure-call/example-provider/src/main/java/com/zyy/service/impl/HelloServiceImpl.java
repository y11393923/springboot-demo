package com.zyy.service.impl;

import com.zyy.service.HelloService;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/29 11:28
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
