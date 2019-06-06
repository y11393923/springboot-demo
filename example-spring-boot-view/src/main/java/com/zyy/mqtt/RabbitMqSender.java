package com.zyy.mqtt;

import com.zyy.config.RibbitQueueConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:58 2019/5/30
 */
@Component
public class RabbitMqSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void convertAndSend(String queue, String content){
        rabbitTemplate.convertAndSend(RibbitQueueConfig.TOPIC_NAME,queue,content);
    }
}
