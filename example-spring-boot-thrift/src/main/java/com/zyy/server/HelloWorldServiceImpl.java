package com.zyy.server;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @create 2018/9/29
 * @since 1.0.0
 */
public class HelloWorldServiceImpl implements HelloWorld.Iface {


    @Override
    public String sendString(String param) throws TException {
        System.out.println("接收到服务器传来的参数："+param);
        String result="服务端成功收到信息";
        return result;
    }
}
