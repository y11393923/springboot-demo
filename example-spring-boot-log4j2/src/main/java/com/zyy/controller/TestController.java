package com.zyy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 15:36 2019/6/13
 */
@RestController
public class TestController {
    @RequestMapping("/")
    public String test(){
        return "success";
    }
}
