package com.zyy.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 19:02 2019/5/30
 */
@Component
public class RabbitMqReceiver {
    private final Logger log = LoggerFactory.getLogger(RabbitMqReceiver.class);
    @RabbitListener(queues = "log")
    public void handler(String message){
        log.info(message);
        //System.out.println("接收到消息："+message);
    }

}
