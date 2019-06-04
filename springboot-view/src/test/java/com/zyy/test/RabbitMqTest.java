package com.zyy.test;

import com.zyy.mqtt.RabbitMqSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 19:01 2019/5/30
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitMqTest {
    @Autowired
    private RabbitMqSender rabbitMqSender;

    @Test
    public void convertAndSend(){
        rabbitMqSender.convertAndSend("sss.log.sss","这是一条测试消息");
    }
}
