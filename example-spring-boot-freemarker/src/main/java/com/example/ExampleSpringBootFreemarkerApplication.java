package com.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ExampleSpringBootFreemarkerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExampleSpringBootFreemarkerApplication.class, args);
        new ExampleSpringBootFreemarkerApplication().demo();
    }


    private void demo() throws Exception {
        //創建freeMarker配置实例
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        //获取模板路径
        configuration.setDirectoryForTemplateLoading(new File(this.getClass().getResource("/templates").getPath()));
        //创建数据模型
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("classPath", "com.example");
        dataMap.put("className", "AutoCodeDemo");
        dataMap.put("helloWord", "测试freeMarker的代码生成程序");
        //加载模板
        Template template = configuration.getTemplate("hello.ftl");
        File file = new File(System.getProperty("user.dir") + "/example-spring-boot-freemarker/src/main/java/com/example/AutoCodeDemo.java");
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))){
            //输出数据
            template.process(dataMap, writer);
            writer.flush();
            System.out.println("文件创建成功");
        }
    }

}
