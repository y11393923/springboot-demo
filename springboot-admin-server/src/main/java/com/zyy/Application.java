package com.zyy;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 17:21 2019/5/30
 */
@SpringBootApplication
@EnableAdminServer
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
