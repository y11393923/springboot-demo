package com.zyy.util;

import com.zyy.mapper.StudentMapper;
import org.springframework.context.ApplicationContext;

/**
 * @author zhouyuyang_vendor
 */
public class MapperInstanceUtil {
    private static ApplicationContext context;

    private volatile static StudentMapper studentMapper;

    private MapperInstanceUtil(){
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static StudentMapper getStudentMapper() {
        if (studentMapper == null) {
            synchronized (StudentMapper.class) {
                if (studentMapper == null) {
                    studentMapper = context.getBean(StudentMapper.class);
                }
            }
        }
        return studentMapper;
    }

}
