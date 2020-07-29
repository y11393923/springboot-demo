package com.zyy.rpc;

import java.lang.reflect.Proxy;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/29 15:01
 */
public class RpcConsumer {

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz, String ip, int port){
        ProxyHandler proxyHandler = new ProxyHandler(ip, port);
        return (T) Proxy.newProxyInstance(RpcConsumer.class.getClassLoader(), new Class[]{clazz}, proxyHandler);
    }
}
