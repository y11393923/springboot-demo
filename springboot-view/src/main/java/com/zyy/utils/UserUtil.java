package com.zyy.utils;

import com.zyy.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:59 2019/6/3
 */
public final class UserUtil {
    /**
     * 获取当前登录的用户信息
     * @return
     */
    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 更新springsecurity储存在session中的用户信息
     * @param user
     * @param request
     */
    public static void modifySecurityUser(User user, HttpServletRequest request){
        //1.从HttpServletRequest中获取SecurityContextImpl对象
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        //2.从SecurityContextImpl中获取Authentication对象
        Authentication authentication = securityContextImpl.getAuthentication();
        //3.初始化UsernamePasswordAuthenticationToken实例 ，这里的参数user就是我们要更新的用户信息
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, authentication.getCredentials());
        auth.setDetails(authentication.getDetails());
        //4.重新设置SecurityContextImpl对象的Authentication
        securityContextImpl.setAuthentication(auth);
    }
}
