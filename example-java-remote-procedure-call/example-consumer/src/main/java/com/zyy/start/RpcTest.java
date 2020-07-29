package com.zyy.start;

import com.zyy.rpc.RpcConsumer;
import com.zyy.service.HelloService;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/29 15:25
 */
public class RpcTest {

    public static void main(String[] args) {
        HelloService service = RpcConsumer.getService(HelloService.class, "127.0.0.1", 20006);
        String result = service.hello("rpc");
        System.out.println(result);
    }
}
