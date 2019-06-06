package com.zyy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:14 2019/5/30
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zyy.controller"))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                .description("测试项目接口文档")
                .contact(new Contact("zyy","https://github.com/y11393923/spring-cloud-demo","11393923@qq.com"))
                .version("v1.0")
                .title("API测试文档")
                .license("baidu")
                .licenseUrl("http://www.baidu.com")
                .build());
    }
}
