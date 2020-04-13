package com.zyy.flink;

import com.zyy.entity.Student;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author zhouyuyang_vendor
 */
@Component
public class FlinkStreamJob {
    @Autowired
    private FlinkKafkaConsumer<Student> flinkKafkaConsumer;
    @Autowired
    private StudentWindowFunction studentWindowFunction;
    @Autowired
    private StudentSinkFunction studentSinkFunction;
    @Autowired
    private StudentWindowTwoFunction studentWindowTwoFunction;
    @Autowired
    private TimeStampExtractor timeStampExtractor;
    @Autowired
    private TumblingEventTimeWindowAssigner tumblingEventTimeWindowAssigner;

    public void apply() throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<Student> studentDataStreamSource = environment.addSource(flinkKafkaConsumer).setParallelism(1);
        studentDataStreamSource
                .assignTimestampsAndWatermarks(timeStampExtractor)
                .flatMap((FlatMapFunction<Student, Student>) (student, collector) -> {
                    //student.setPhone(student.getPhone().substring(8));
                    collector.collect(student);
                }).returns(Student.class)
                .keyBy(Student::getSex)
                .window(tumblingEventTimeWindowAssigner)
                //.timeWindow(Time.seconds(20))
                .apply(studentWindowTwoFunction)
                //.sum("phone")
                .print();
        /*studentDataStreamSource0
                .timeWindowAll(Time.minutes(1))
                .apply(studentWindowFunction)
                .addSink(studentSinkFunction);*/
        environment.execute();
    }
}
