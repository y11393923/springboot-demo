package com.example.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 16:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ExcelColumn {
    String name() default "";

    String dataFormat() default "";

    String separate() default "";

    String mergedSpan() default "{\"left\":1}";
}
