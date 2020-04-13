
package com.zyy.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * @author xuezhiqiang1@sensetime.com
 * @date 2019/9/10  11:51
 */
@Component
@Slf4j
public class TumblingEventTimeWindowAssigner extends WindowAssigner<Object, TimeWindow> {

    private static final long serialVersionUID = -1589847074316059113L;

    @Override
    public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
        if (timestamp > Long.MIN_VALUE) {
            // Long.MIN_VALUE is currently assigned when no timestamp is present
            long start = TimeWindow.getWindowStartWithOffset(timestamp, 0, 20000);
            log.info("assignWindows start time : {}", start);
            return Collections.singletonList(new TimeWindow(start, start + 20000));
        } else {
            throw new RuntimeException("Record has Long.MIN_VALUE timestamp (= no timestamp marker). " +
                    "Is the time characteristic set to 'ProcessingTime', or did you forget to call " +
                    "'DataStream.assignTimestampsAndWatermarks(...)'?");
        }
    }


    public static TumblingEventTimeWindowAssigner of() {
        return new TumblingEventTimeWindowAssigner();
    }

    @Override
    public Trigger<Object, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
        //return ProcessingTimeTrigger.create();
        return EventTimeTrigger.create();
    }

    @Override
    public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
        return new TimeWindow.Serializer();
    }

    @Override
    public boolean isEventTime() {
        return true;
    }


}
