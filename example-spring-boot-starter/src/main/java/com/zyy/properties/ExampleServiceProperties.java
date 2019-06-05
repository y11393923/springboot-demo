package com.zyy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:50 2019/6/5
 */
@ConfigurationProperties("example.service")
public class ExampleServiceProperties {
    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
