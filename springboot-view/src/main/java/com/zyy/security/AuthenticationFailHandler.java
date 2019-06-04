package com.zyy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyy.enums.ResultCode;
import com.zyy.response.ResponseResult;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author:zhouyuyang
 * @Description:    登录失败的业务处理
 * @Date: Created in 10:45 2019/6/3
 */
@Component
public class AuthenticationFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ResponseResult respBean ;
        if (e instanceof BadCredentialsException ||
                e instanceof UsernameNotFoundException) {
            respBean = new ResponseResult(ResultCode.ERROR_ACCOUNT_NAME_OR_PASSWORD);
        } else if (e instanceof LockedException) {
            respBean = new ResponseResult(ResultCode.THE_ACCOUNT_IS_LOCKED);
        } else if (e instanceof CredentialsExpiredException) {
            respBean = new ResponseResult(ResultCode.PASSWORD_EXPIRES);
        } else if (e instanceof AccountExpiredException) {
            respBean = new ResponseResult(ResultCode.ACCOUNT_EXPIRES);
        } else if (e instanceof DisabledException) {
            respBean = new ResponseResult(ResultCode.ACCOUNT_DISABLED);
        } else {
            respBean = new ResponseResult(ResultCode.ERROR_ACCOUNT_NAME_OR_PASSWORD);
        }
        //response.setStatus(401);
        ObjectMapper om = new ObjectMapper();
        PrintWriter out = response.getWriter();
        out.write(om.writeValueAsString(respBean));
        out.flush();
        out.close();
    }
}
