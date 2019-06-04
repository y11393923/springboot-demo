package com.zyy.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:10 2019/5/27
 */
@Configuration
public class JdbcTemplateConfig {
    @Bean
    JdbcTemplate jdbcTemplateOne(@Qualifier("dsOne")DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
    @Bean
    JdbcTemplate jdbcTemplateTwo(@Qualifier("dsTwo")DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
