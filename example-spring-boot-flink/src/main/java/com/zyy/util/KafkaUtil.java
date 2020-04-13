package com.zyy.util;

import com.alibaba.fastjson.JSON;
import com.zyy.entity.Student;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author zhouyuyang_vendor
 */
public class KafkaUtil {
    public static final String broker_list = "10.111.32.74:10209";
    public static final String topic = "WhaleCrowdCollect";

    public static void writeToKafka() throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        for (int i = 1; i <= 1000000; i++) {
            Student student = Student.builder().name("lisi"+i).sex(i % 2 == 0?"男":"女").phone((long)i).build();
            ProducerRecord record = new ProducerRecord<String, byte[]>(topic, null, null, JSON.toJSONString(student).getBytes());
            producer.send(record);
            System.out.println("发送数据: " + JSON.toJSONString(student));
            //发送一条数据 sleep 10s，相当于 1 分钟 6 条
            Thread.sleep(1 * 1000);
        }
        producer.flush();
    }

    public static void main(String[] args) throws InterruptedException {
        writeToKafka();
    }
}
