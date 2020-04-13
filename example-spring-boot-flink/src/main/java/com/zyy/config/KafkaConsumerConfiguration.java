package com.zyy.config;

import com.zyy.entity.Student;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer.KEY_POLL_TIMEOUT;

/**
 * @author zhouyuyang_vendor
 */
@Configuration
public class KafkaConsumerConfiguration {

    @Autowired
    private KafkaConsumerProperties consumerProperties;


    @Bean
    public FlinkKafkaConsumer<Student> flinkKafkaConsumer(){
        return new FlinkKafkaConsumer<>(consumerProperties.getTopics(), new StudentDeserializeSchema(), consumerProps(consumerProperties));
    }

    public Properties consumerProps(KafkaConsumerProperties consumerProperties) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getBootstrapServers());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProperties.getEnableAutoCommit());
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerProperties.getAutoCommitInterval());
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerProperties.getSessionTimeOut());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getGroupId());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProperties.getAutoOffsetReset());
        properties.put(KEY_POLL_TIMEOUT,consumerProperties.getPollTimeOut());
        return properties;
    }
}
