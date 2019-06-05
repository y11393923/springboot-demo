package com.zyy;

import com.zyy.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 11:22 2019/6/5
 */
@SpringBootApplication
@RestController
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Autowired
    private ExampleService exampleService;

    @GetMapping(value = "/")
    public String test(String param){
        return exampleService.helloStarterExample(param);
    }
}
