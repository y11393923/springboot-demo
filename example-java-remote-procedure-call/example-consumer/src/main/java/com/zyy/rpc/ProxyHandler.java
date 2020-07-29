package com.zyy.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 建立socket连接
 * 封装请求数据，发送给服务提供者
 * 返回结果
 * @Author: zhouyuyang
 * @Date: 2020/7/29 15:06
 */
public class ProxyHandler implements InvocationHandler {
    private String ip;
    private int port;

    public ProxyHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Socket socket = new Socket(ip, port);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        output.writeObject(proxy.getClass().getInterfaces()[0]);
        output.writeUTF(method.getName());
        output.writeObject(method.getParameterTypes());
        output.writeObject(args);
        output.flush();
        Object result = input.readObject();
        output.close();
        input.close();
        socket.close();
        if (result instanceof Throwable) {
            throw (Throwable) result;
        }
        return result;
    }
}
