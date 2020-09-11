package com.example.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class JacksonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    static {
        // 全部字段序列化
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * @param obj
     * @return
     */
    public static <T> String toJsonString(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("error to parse object to json string", e);
            return null;
        }
    }

    /**
     * 有格式的
     *
     * @param obj
     * @return
     */
    public static <T> String toPrettyJson(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("error to parse object to pretty json string", e);
            return null;
        }
    }

    /**
     * 字符串转对象
     *
     * @param str
     * @param clazz
     * @return
     */
    public static <T> T toObject(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            logger.error("error to parse str to class {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * 字段符转List之类的集合
     *
     * @param jsonStr       json字符串
     * @param typeReference 运行时可以获取泛型参数
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T string2Obj(String jsonStr, TypeReference typeReference) {
        if (StringUtils.isEmpty(jsonStr) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonStr : objectMapper.readValue(jsonStr, typeReference));
        } catch (Exception e) {
            logger.error("error to parse str to type {}", typeReference.getType(), e);
            return null;
        }
    }

    /**
     * json字符转化为集合对象
     *
     * @param jsonStr      json字符串
     * @param elementClass 集合元素类型
     * @param <T>          泛型参数
     * @return 返回含有该对象的List集合
     */
    public static <T> List<T> toJSONArray(String jsonStr, Class<T> elementClass) {
        return string2Obj(jsonStr, List.class, elementClass);
    }

    /**
     * 字段符转List之类的集合
     *
     * @param jsonStr
     * @param collectionClass
     * @param elementClasses
     * @return
     */
    public static <T> T string2Obj(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(jsonStr, javaType);
        } catch (Exception e) {
            String[] elementNames = Arrays.stream(elementClasses).map(Class::getName).collect(Collectors.toList()).toArray(new String[]{});
            logger.error("fail to parse jsonStr to collection {} with element class {}",
                    collectionClass.getName(), String.join(",", elementNames), e);
            return null;
        }
    }

}
