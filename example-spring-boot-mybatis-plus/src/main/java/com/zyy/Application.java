package com.zyy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:26 2019/5/20
 */
@SpringBootApplication
@MapperScan("com.zyy.dao.*")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}
