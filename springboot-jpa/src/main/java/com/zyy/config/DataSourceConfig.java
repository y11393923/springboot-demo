package com.zyy.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:07 2019/5/27
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary    //必须加上此注解不然启动报错
    @ConfigurationProperties("spring.datasource.one")
    DataSource dsOne(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.two")
    DataSource dsTwo(){
        return DruidDataSourceBuilder.create().build();
    }

}
