package com.zyy.security;

import com.zyy.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;


/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:33 2019/5/31
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private AuthenticationFailHandler authenticationFailHandler;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;
    @Autowired
    private CustomMetadataSource customMetadataSource;
    @Autowired
    private UrlAccessDecisionManager urlAccessDecisionManager;

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //所有用户均可访问的资源不需要权限
        web.ignoring().antMatchers( "/favicon.ico","/images/**","/error/**","/index","/login.html","/userLogin"
                , "/register", "/register.html", "/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭csrf，拦截所有请求
        http.csrf().disable()
                .authorizeRequests()
                //所有用户均可访问的资源   写在这里会出现死循环认证，重定向的次数过多。
                /*.antMatchers( "/favicon.ico","/images/**","/error/**","/index","/login.html"
                        ,"/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**")
                .permitAll()*/
                // http所有的请求必须通过授权认证才可以访问。
                .anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setSecurityMetadataSource(customMetadataSource);
                        o.setAccessDecisionManager(urlAccessDecisionManager);
                        return o;
                    }
                })
                .and()
                //设置登录的页面和登录拦截的接口   formLogin().permitAll()方法允许所有用户基于表单登录访问/login.html这个page。
                .formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll()
                //设置用户名跟密码字段  默认为username,password
                .usernameParameter("userName").passwordParameter("password")
                //登录成功 返回json信息  可以通过defaultSuccessUrl()来设置登录成功跳转页面  通过failureForwardUrl()来设置登录失败的页面
                .successHandler(authenticationSuccessHandler)
                //登录失败的处理返回json
                .failureHandler(authenticationFailHandler)
                .permitAll()
                .and()
                //设置注销登录的拦截路径 默认为/logout,注销成功设置返回页面
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login.html")
                .permitAll()
                .and()
                //设置权限不够返回的json   通过accessDeniedPage()设置页面
                .exceptionHandling().accessDeniedHandler(authenticationAccessDeniedHandler);

    }
}
