package com.zyy.flink;

import com.google.common.collect.Lists;
import com.zyy.entity.Student;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author zhouyuyang_vendor
 */
@Component
public class StudentWindowFunction implements AllWindowFunction<Student , List<Student>, TimeWindow> {


    @Override
    public void apply(TimeWindow timeWindow, Iterable<Student> values, Collector<List<Student>> collector) throws Exception {
        List<Student> students = Lists.newArrayList(values);
        if (students.size() > 0) {
            System.out.println("1 分钟内收集到 student 的数据条数是：" + students.size());
            collector.collect(students);
        }
    }
}
