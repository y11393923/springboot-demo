package com.zyy.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:46 2019/5/30
 */
@Configuration
public class RibbitQueueConfig {
    public final static String TOPIC_NAME = "test-topic";

    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_NAME,true,false);
    }
    @Bean
    Queue log(){
        return new Queue("log");
    }

    /**
     * 只要消息的routingkey中包含log都讲被路由到名称为log的Queue上
     * @return
     */
    @Bean
    Binding logBinding(){
        return BindingBuilder.bind(log()).to(topicExchange()).with("#.log.#");
    }
}
