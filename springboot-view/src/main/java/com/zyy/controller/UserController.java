package com.zyy.controller;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.zyy.entity.User;
import com.zyy.enums.ResultCode;
import com.zyy.exception.CustomException;
import com.zyy.response.ResponseResult;
import com.zyy.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 11:04 2019/5/27
 */
@Controller
@Api(tags = "用户信息接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;


    @ApiIgnore
    @RequestMapping(value = "/user")
    public String test(Model model){
        List<User> userList = userService.findAll();
        model.addAttribute("userList",userList);
        return "index";
    }



    @ApiIgnore
    @RequestMapping(value = "/testError")
    @ResponseBody
    public String testError(){
        throw  new CustomException(ResultCode.FAIL);
        //return "{\"name\":\"lisi\"}";
    }


    @ApiOperation(value = "新增用户" , notes = "新增用户接口 传入json参数")
    @ApiResponses(value = {
            @ApiResponse(code = 200,message = "操作正常"),
            @ApiResponse(code = 500,message = "服务器错误")})
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseBody
    public String addUser(@RequestBody User user){
        return user.toString();
    }

    @ApiOperation(value = "用户注册" , notes = "用户注册接口 传入用户名和密码")
    @ApiResponses(value = {
            @ApiResponse(code = 200,message = "操作正常"),
            @ApiResponse(code = 401,message = "参数错误"),
            @ApiResponse(code = 500,message = "服务器错误")})
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult addUser(String userName, String password){
        return userService.register(userName,password);
    }

    /**
     * 自定义登录  暂不使用
     * @param request
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/userLogin")
    public String userLogin(HttpServletRequest request) {

        String username = request.getParameter("userName");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try{
            //使用SpringSecurity拦截登陆请求 进行认证和授权
            Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authenticate);
            //使用redis session共享
            HttpSession session = request.getSession();
            //这个非常重要，否则验证后将无法登陆
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:login.html";
        }
        return "redirect:index";
    }

}
