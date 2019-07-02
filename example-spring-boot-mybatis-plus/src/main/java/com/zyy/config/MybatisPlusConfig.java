package com.zyy.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:43 2019/5/20
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.zyy.dao.*")
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

    /**
     * SQL 执行性能分析，开发环境使用，线上不推荐。
     */
    @Bean
    @Profile({"dev", "test"})   // 设置 dev test 环境开启
    public PerformanceInterceptor performanceInterceptor(){
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        //SQL是否格式化 默认false
        performanceInterceptor.setFormat(false);
        // maxTime 指的是 sql 最大执行时长 超过自动停止运行，有助于发现问题
        performanceInterceptor.setMaxTime(300);
        return performanceInterceptor;
    }

    /**
     * 乐观锁插件
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){
        return new OptimisticLockerInterceptor();
    }
}
