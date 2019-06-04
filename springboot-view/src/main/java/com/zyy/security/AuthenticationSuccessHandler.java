package com.zyy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyy.enums.ResultCode;
import com.zyy.response.ResponseResult;
import com.zyy.utils.UserUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author:zhouyuyang
 * @Description:    登录成功的业务处理
 * @Date: Created in 10:57 2019/6/3
 */
@Component
public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ResponseResult respBean = new ResponseResult(ResultCode.LOGIN_SUCCESSFULLY, UserUtil.getCurrentUser());
        ObjectMapper om = new ObjectMapper();
        PrintWriter out = response.getWriter();
        out.write(om.writeValueAsString(respBean));
        out.flush();
        out.close();
    }
}
