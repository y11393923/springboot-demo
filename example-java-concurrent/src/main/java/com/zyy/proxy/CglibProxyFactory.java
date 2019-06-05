package com.zyy.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 14:35 2019/4/30
 */
public class CglibProxyFactory implements MethodInterceptor {
    private Object target;

    public CglibProxyFactory(Object target) {
        this.target = target;
    }

    public Object getProxyInstance(){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(target.getClass());
        //设置回调函数
        enhancer.setCallback(this);
        //创建子类(代理对象)
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("开始事务...");

        Object invoke = method.invoke(target, objects);

        System.out.println("提交事务...");
        return invoke;
    }
}
