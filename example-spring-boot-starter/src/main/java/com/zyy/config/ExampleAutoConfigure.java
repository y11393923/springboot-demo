package com.zyy.config;

import com.zyy.properties.ExampleServiceProperties;
import com.zyy.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:zhouyuyang
 * @Description:    @ConditionalOnClass注解表示当classpath下发现该类的情况下进行实例化自动配置。
 * @                @ConditionalOnMissingBean注解表示仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean
 * @                @EnableConfigurationProperties注解表示使使用 @ConfigurationProperties 注解的类生效。
 * @                @ConditionalOnProperty注解表示配置了example.service.enabled=true 则该configuration生效；为false则不生效。
 * @Date: Created in 10:57 2019/6/5
 */
@Configuration
@ConditionalOnClass(ExampleService.class)
@EnableConfigurationProperties(ExampleServiceProperties.class)
public class ExampleAutoConfigure {
    @Autowired
    private ExampleServiceProperties properties;

    @Bean
    @ConditionalOnMissingBean
    //@ConditionalOnProperty(prefix = "example.service",value = "enabled",havingValue = "true")
    ExampleService exampleService(){
        return new ExampleService(properties.getPrefix(),properties.getSuffix());
    }
}
