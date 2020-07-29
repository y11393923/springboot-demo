package com.zyy.start;

import com.zyy.rpc.RpcProvider;
import com.zyy.service.HelloService;
import com.zyy.service.impl.HelloServiceImpl;

import java.io.IOException;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/29 11:30
 */
public class RpcBootsrap {

    public static void main(String[] args) throws IOException {
        HelloService helloService = new HelloServiceImpl();
        RpcProvider.export(20006, helloService);
    }
}
