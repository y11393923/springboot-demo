package com.zyy.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author:zhouyuyang
 * @Description:    jdk的proxy动态代理 代理对象不需要实现接口,但是目标对象一定要实现接口,否则不能用动态代理
 * @Date: Created in 12:06 2019/4/30
 */
public class JDKProxyFactory {
    /**
     * 维护一个目标对象
     */
    private Object target;

    public JDKProxyFactory(Object target){
        this.target=target;
    }

    /**
     * 给目标对象生成代理对象
     * @return
     */
    public Object getProxyInstance(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("开始事务");
                Object invoke = method.invoke(target, args);
                System.out.println("提交事务");
                return invoke;
            }
        });
    }
}
