package com.zyy.rpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * 将需要发布的服务存储在一个内存变量serviceList中
 * 启动socket，server.accept()方法阻塞在那，监听输入
 * 针对每一个请求，单独启动一个线程处理
 * @Author: zhouyuyang
 * @Date: 2020/7/29 11:28
 */
public class RpcProvider {

    /**
     * 储存注册的服务列表
     */
    private static List<Object> serviceList;

    /**
     * 发布rpc服务
     * @param port
     * @param services
     */
    public static void export(int port, Object... services) throws IOException {
        serviceList = Arrays.asList(services);
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket;
        while (true){
            //阻塞等待输入
            socket = serverSocket.accept();
            //每个请求，启动一个新的线程处理
            new Thread(new ServerThread(socket, serviceList)).start();
        }
    }
}
