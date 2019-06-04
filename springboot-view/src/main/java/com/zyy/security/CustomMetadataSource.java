package com.zyy.security;

import com.zyy.entity.Menu;
import com.zyy.entity.Role;
import com.zyy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:    主要就是当访问一个url时返回这个url所需要的访问权限。
 * @Date: Created in 15:27 2019/6/3
 */
@Component
public class CustomMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Autowired
    private MenuService menuService;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 返回本次访问需要的权限，可以有多个权限
     * @param o
     * @return
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) o).getRequestUrl();
        List<Menu> allMenu = menuService.getAllMenu();
        if (!CollectionUtils.isEmpty(allMenu)){
            for (Menu menu : allMenu){
                if (antPathMatcher.match(menu.getUrl(), requestUrl)
                        && menu.getRoles().size() > 0) {
                    List<Role> roles = menu.getRoles();
                    int size = roles.size();
                    String[] values = new String[size];
                    for (int i = 0; i < size; i++) {
                        values[i] = roles.get(i).getName();
                    }
                    return SecurityConfig.createList(values);
                }
            }
        }
        //没有匹配上的资源，都是登录访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    /**
     * 如果返回了所有定义的权限资源，Spring Security会在启动时校验每个ConfigAttribute是否配置正确，不需要校验直接返回null。
     * @return
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    /**
     * 返回类对象是否支持校验，web项目一般使用FilterInvocation来判断，或者直接返回true。
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
