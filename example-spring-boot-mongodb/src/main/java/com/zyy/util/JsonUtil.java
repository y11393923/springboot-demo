package com.zyy.util;

import com.alibaba.fastjson.JSON;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @create 2018/9/14
 * @since 1.0.0
 */
public class JsonUtil {
    public static Map<String,Object> jsonStrToMap(String json){
        Map<String,Object> maps=null;
        try {
            maps = JSON.parseObject(json, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }


    public static String entityToJson(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        Map<String,Object> map=new HashMap<String, Object>(fields.length);
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String key = field.getName();
                Method method = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase()+key.substring(1));
                Object value = method.invoke(obj);
                if(value==null){
                    value="";
                }
                map.put(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return JSON.toJSONString(map);
    }

}
