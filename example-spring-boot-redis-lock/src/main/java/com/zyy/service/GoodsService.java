package com.zyy.service;

import com.zyy.config.SnowflakeConfig;
import com.zyy.dao.GoodsDao;
import com.zyy.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @create 2018/9/26
 * @since 1.0.0
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private SnowflakeConfig snowflakeConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${goodLock}")
    private String key;

    public Goods findById(int id) {
        return goodsDao.findById(id);
    }

    /**
     * redis 分布式锁扣减库存
     * @param id
     * @param num
     * @return
     */
    public String updateById(int id, int num) {
        long time=System.currentTimeMillis()+3000;
        while (System.currentTimeMillis()<time){
            String value = snowflakeConfig.nextId().toString();
            Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, value);
            if (ifAbsent){
                int result=0;
                redisTemplate.opsForValue().set(key,value,30,TimeUnit.SECONDS);
                Goods good = goodsDao.findById(id);
                if (good.getNumber() >= num){
                    result=goodsDao.updateById(good.getId(),num);
                }
                String values= redisTemplate.opsForValue().get(key);
                if(values.equals(value)){
                    redisTemplate.delete(key);
                }
                if(result > 0){
                    return "success";
                }
            }
        }
        return "fail";
    }
}
