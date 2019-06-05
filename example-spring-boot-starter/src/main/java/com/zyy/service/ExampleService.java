package com.zyy.service;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:52 2019/6/5
 */
public class ExampleService {
    private String prefix;
    private String suffix;

    public ExampleService(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String helloStarterExample(String param){
        return  prefix + " hello starter " +  param + " " + suffix;
    }
}
