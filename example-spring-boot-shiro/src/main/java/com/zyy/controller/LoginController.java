package com.zyy.controller;

import com.zyy.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


/**
 * 功能描述：LoginController
 */
@RestController
public class LoginController {

    /**
     * 描述：session对象
     * @date 2018/12/9 20:35
     */
    @Autowired
    private HttpSession session;

    /**
     * 功能描述：login，即shiro的认证
     * @param user
     * @return org.springframework.http.ResponseEntity<java.lang.Void>
     **/
    @GetMapping("/login")
    public ResponseEntity<Void> login(User user){
        //1 接收页面参数，转成对象----系统自动完成了
        //2 获取Subject对象
        Subject subject = SecurityUtils.getSubject();
        //3 Subject启动Shiro
        // 准备数据
        UsernamePasswordToken upToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try{
            subject.login(upToken);
            // 能够执行到这步，肯定登录已经成功
            // 获取用户信息，保存到session中
            User loginUser = (User) subject.getPrincipal();
            // 放入session中
            session.setAttribute("loginUser",loginUser);
            //返回状态
            return new ResponseEntity<>(HttpStatus.OK);

        } catch ( UnknownAccountException uae ) {
            //用户名未知...
            System.out.println("用户不存在");
        } catch ( IncorrectCredentialsException ice ) {
            //凭据不正确，例如密码不正确 ...
            System.out.println("密码不正确");
        } catch ( LockedAccountException lae ) {
            //用户被锁定，例如管理员把某个用户禁用...
            System.out.println("用户被禁用");
        } catch ( ExcessiveAttemptsException eae ) {
            //尝试认证次数多余系统指定次数 ...
            System.out.println("请求次数过多，用户被锁定");
        } catch ( AuthenticationException ae ) {
            //其他未指定异常
            System.out.println("未知错误，无法完成登录");
        }
        //返回状态
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 功能描述： shiro的logout退出，只是将放置PrincipalCollection这个集合置空，
     * 删除了session，但是没有清空缓存,需要手动清除缓存
     * @return org.springframework.http.ResponseEntity<java.lang.Void>
     **/
    @PutMapping("/logout")
    public ResponseEntity<Void> logout(){
        //清空session
        session.removeAttribute("loginUser");
        //退出shiro
        SecurityUtils.getSubject().logout();
        //返回状态
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
