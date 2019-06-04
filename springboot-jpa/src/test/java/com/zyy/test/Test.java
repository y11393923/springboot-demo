package com.zyy.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:02 2019/5/29
 */
@SpringBootTest
//RunWith注解将Junit执行类改成SpringRunner
@RunWith(SpringRunner.class)
public class Test {

    @org.junit.Test
    public void test(){

    }
}
