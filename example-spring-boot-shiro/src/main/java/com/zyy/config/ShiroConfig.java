package com.zyy.config;


import com.zyy.shiro.ShiroCredentialsMatcher;
import com.zyy.shiro.ShiroRealm;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 功能描述: 在ShiroConfig中做什么事情呢？
 * 1 配置shiro安全管理器，向安全管理器中注入Realm域
 * 2 配置Realm域：注入密码比较器
 * 3 配置密码比较器
 * 4 配置拦截路径和放行路径
 */
@Configuration
public class ShiroConfig {

    /**
     * 配置安全管理器，并且注入Realm域
     * @param realm
     */
    @Bean
    public SecurityManager securityManager(Realm realm){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    /**
     *  Credentials：凭证/证书 ---
     *
     * 配置Realm域，注入密码比较器
     * @param credentialsMatcher
     */
    @Bean
    public ShiroRealm realm(CredentialsMatcher credentialsMatcher){
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(credentialsMatcher);
        return shiroRealm;
    }
    /**
     * 密码比较器
     */
    @Bean
    public CredentialsMatcher credentialsMatcher(){
        return new ShiroCredentialsMatcher();
    }

    /**
     * 配置拦截路径和放行路径
     * @param securityManager
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        // shiro过滤器工厂类
        ShiroFilterFactoryBean shiroFilterFactoryBean  = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器----Map集合
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/login*", "anon");
        filterChainDefinitionMap.put("/index.html*", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        //根据用户角色赋予相应的权限
        filterChainDefinitionMap.put("/add-success.html", "roles[Commom]");
        filterChainDefinitionMap.put("/selete-success.html", "roles[Commom]");
        filterChainDefinitionMap.put("/update-success.html", "roles[Member]");
        filterChainDefinitionMap.put("/delete-success.html", "roles[Vip]");
        //根据用户拥有的具体权限赋予相应的权限
        filterChainDefinitionMap.put("/add-success.html", "perms[add]");
        filterChainDefinitionMap.put("/selete-success.html", "perms[select]");
        filterChainDefinitionMap.put("/update-success.html", "perms[update]");
        filterChainDefinitionMap.put("/delete-success.html", "perms[delete]");
        //   /** 匹配所有的路径
        //  通过Map集合组成了一个拦截器链 ，自顶向下过滤，一旦匹配，则不再执行下面的过滤
        //  如果下面的定义与上面冲突，那按照了谁先定义谁说了算
        //  /** 一定要配置在最后     authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        filterChainDefinitionMap.put("/**", "authc");
        // 将拦截器链设置到shiro中
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/index.html");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/button.html");
        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/power.html");

        return shiroFilterFactoryBean;
    }

    /**
     * 开启shiro aop注解支持
     * 使用代理方式;所以需要开启代码支持
     * @param securityManager
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    /**
     * 开启cglib代理
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
}
