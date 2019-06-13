package com.zyy;

import com.zyy.client.HelloWorldClient;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 14:55 2019/6/13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestThrift {
    @Autowired
    private HelloWorldClient client;
    @Test
    public void testSend(){
        client.open();
        String result="";
        try {
            result=client.getClient().sendString("hello");
        } catch (TException e) {
            e.printStackTrace();
        }
        client.close();
        System.out.println(result);
    }
}
