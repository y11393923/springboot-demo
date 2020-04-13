package com.zyy.flink;

import com.google.common.collect.Lists;
import com.zyy.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhouyuyang_vendor
 */
@Component
@Slf4j
public class StudentWindowTwoFunction extends RichWindowFunction<Student, List<Student>, String, TimeWindow> {


    private static final long serialVersionUID = -5418270226849683857L;

    @Override
    public void apply(String s, TimeWindow window, Iterable<Student> values, Collector<List<Student>> collector) throws Exception {
        List<Student> students = Lists.newArrayList(values);
       // Map<Integer, List<String>> collect = students.stream().collect(Collectors.groupingBy(Student::getId, Collectors.mapping(Student::getName, Collectors.toList())));
        log.info("收集到 student 的数据条数是：" + students.size());
        collector.collect(students);
    }
}