package com.zyy.config;

import com.alibaba.fastjson.JSON;
import com.zyy.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;


/**
 * @author zhouyuyang_vendor
 */
@Slf4j
public class StudentDeserializeSchema implements DeserializationSchema<Student>, SerializationSchema<Student> {

    @Override
    public Student deserialize(byte[] bytes) {
        String message= new String(bytes);
        log.debug("receive message|message:{}", message);
        return JSON.parseObject(message, Student.class);
    }

    @Override
    public boolean isEndOfStream(Student student) {
        return false;
    }

    @Override
    public byte[] serialize(Student student) {
        return JSON.toJSONString(student).getBytes();
    }

    @Override
    public TypeInformation<Student> getProducedType() {
        return TypeExtractor.getForClass(Student.class);
    }
}
