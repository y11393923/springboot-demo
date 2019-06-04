package com.zyy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 12:02 2019/5/27
 */
@Controller
@ApiIgnore
public class PageController {

    @RequestMapping("/upload.html")
    public String uploadPage(){
        return "upload";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/login.html")
    public String loginPage(){
        return "login";
    }
}
