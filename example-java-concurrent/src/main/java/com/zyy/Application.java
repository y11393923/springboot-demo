package com.zyy;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.zyy.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@RestController
public class Application {

    @Resource
    RedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    @RequestMapping("/test")
    public Object test(){
        final Long[] ids=new Long[]{1L,2L};
        List<User> list =redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for (int i = 0; i < ids.length; i++) {
                    redisConnection.get(redisTemplate.getKeySerializer().serialize(redisUserKey(ids[i].toString())));
                }
                return null;
            }
        });
        List<User> userList=new ArrayList<User>();
        List<Long> noCache=new ArrayList<Long>();
        for (int i=0;i<ids.length;i++){
            User userinfo = list.get(i);
            if (null==userinfo){
                noCache.add(ids[i]);
            }else{
                userList.add(userinfo);
            }
        }
        for (Long id:noCache) {
            final User user = new User(id, "张三" + id, new Date());
            userList.add(user);
            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    redisConnection.set(redisTemplate.getKeySerializer().serialize(redisUserKey(user.getId().toString())),redisTemplate.getValueSerializer().serialize(user));
                    return null;
                }
            });
        }
        return userList;
    }

    private String redisUserKey(String key){
        String format = String.format("user:info:%s", key);
        return format;
    }

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverter(){
        //创建FastJson信息转换对象
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter=new FastJsonHttpMessageConverter();
        //创建Fastjosn对象并设定序列化规则
        FastJsonConfig fastJsonConfig=new FastJsonConfig();
        //中文乱码解决方案
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        //设定json格式且编码为UTF-8
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        //输出为null的字段   默认不输出
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        //格式化
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //规则赋予转换对象
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastJsonHttpMessageConverter);
    }


}
