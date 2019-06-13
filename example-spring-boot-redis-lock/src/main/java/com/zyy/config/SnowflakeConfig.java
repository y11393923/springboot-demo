package com.zyy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @create 2018/9/26
 * @since 1.0.0
 */
@Configuration
public class SnowflakeConfig {
    @Autowired
    private IdWorker idWorker;

    @Bean
    public IdWorker getIdWorker(){
        return new IdWorker(1,1,1);
    }

    public Long nextId(){
        return idWorker.nextId();
    }
}
