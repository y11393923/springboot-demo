package com.zyy;

import com.zyy.flink.FlinkStreamJob;
import com.zyy.util.MapperInstanceUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zhouyuyang_vendor
 */
@SpringBootApplication
public class ExampleSpringBootFlinkApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleSpringBootFlinkApplication.class, args);
        MapperInstanceUtil.setApplicationContext(context);
        FlinkStreamJob flinkStreamJob = context.getBean(FlinkStreamJob.class);
        flinkStreamJob.apply();
        SpringApplication.exit(context);
    }

}
