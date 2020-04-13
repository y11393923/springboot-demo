package com.zyy.flink;

import com.zyy.entity.Student;
import com.zyy.mapper.StudentMapper;
import com.zyy.util.MapperInstanceUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhouyuyang_vendor
 */
@Component
public class StudentSinkFunction extends RichSinkFunction<List<Student>> {

    private StudentMapper studentMapper;

    @Override
    public void invoke(List<Student> value, Context context) throws Exception {
        value.forEach(student -> studentMapper.insert(student));
        System.out.println("成功插入了"+value.size()+"数据");
    }


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        studentMapper = MapperInstanceUtil.getStudentMapper();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
