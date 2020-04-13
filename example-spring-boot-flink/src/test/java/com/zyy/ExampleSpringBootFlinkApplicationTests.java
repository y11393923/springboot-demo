package com.zyy;

import com.zyy.entity.Student;
import com.zyy.mapper.StudentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleSpringBootFlinkApplicationTests {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    void contextLoads() {
        studentMapper.insert(Student.builder().name("zhangsan").sex("男").phone(123L).build());
    }

}
