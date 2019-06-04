package com.zyy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyy.enums.ResultCode;
import com.zyy.response.ResponseResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author:zhouyuyang
 * @Description:    权限不足的业务处理
 * @Date: Created in 11:08 2019/6/3
 */
@Component
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ResponseResult error = new ResponseResult(ResultCode.INSUFFICIENT_PRIVILEGES);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();
    }
}
