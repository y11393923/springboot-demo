package com.zyy.shiro;

import com.zyy.util.Encrypt;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * 功能描述：shiro认证器：ShiroCredentialsMatcher
 */
public class ShiroCredentialsMatcher extends SimpleCredentialsMatcher {

    
    /**
     * 功能描述：shiro的密码比较器
     * @param token : 用户页面输入的信息
     * @param info : 数据库中的信息
     * @return boolean
     **/
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //获取用户输入的用户名和密码
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        //得到密码凭证
        char[] pwd = upToken.getPassword();
        //转换为string类型
        String myPwd = new String(pwd);
        //将用户输入的密码转为密文
        String newPwd = Encrypt.md5(myPwd, upToken.getUsername());
        //获取数据库中的密文密码
        Object dbPwd = info.getCredentials();
        //密码验证
        return equals(newPwd, dbPwd);
    }
}
