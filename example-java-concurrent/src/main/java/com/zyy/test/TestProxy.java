package com.zyy.test;

import com.zyy.dao.UserDao;
import com.zyy.dao.impl.UserDaoImpl;
import com.zyy.entity.User;
import com.zyy.proxy.CglibProxyFactory;
import com.zyy.proxy.JDKProxyFactory;

import java.util.Date;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 14:09 2019/4/30
 */
public class TestProxy {
    public static void main(String[] args) {
        UserDao target=new UserDaoImpl();
        System.out.println(target.getClass());
        UserDao proxy=(UserDao)new JDKProxyFactory(target).getProxyInstance();
        System.out.println(proxy.getClass());
        proxy.save(new User(1L,"张三",new Date()));

        UserDaoImpl target2=new UserDaoImpl();
        UserDaoImpl proxy2=(UserDaoImpl) new CglibProxyFactory(target2).getProxyInstance();
        System.out.println(proxy2.getClass());
        proxy2.save(new User(2L,"李四",new Date()));

    }
}
