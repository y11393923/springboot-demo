package com.zyy.flink;

import com.zyy.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 * @author xuezhiqiang1@sensetime.com
 * @date 2019/9/11  17:28
 */
@Component
@Slf4j
public class TimeStampExtractor implements AssignerWithPeriodicWatermarks<Student> {

    private static final long serialVersionUID = 4035126854518496341L;

    private volatile long currentTimestamp = Long.MIN_VALUE;

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1000);
    }

    @Override
    public long extractTimestamp(Student student, long previousElementTimestamp) {
        long currentTime = System.currentTimeMillis() - 1000;
        if(currentTime > currentTimestamp) {
            currentTimestamp = currentTime;
        }
        return currentTime;
    }
}
