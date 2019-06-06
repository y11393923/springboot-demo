package com.zyy.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 11:10 2019/5/31
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testRedis() {
        redisTemplate.opsForValue().set("key", "value");
        Object value = redisTemplate.opsForValue().get("key");
        System.out.println(value);
    }
}
