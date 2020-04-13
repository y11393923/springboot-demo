package com.zyy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author zhouyuyang_vendor
 */
@Configuration
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {
    private String bootstrapServers;
    private String groupId;
    private List<String> topics;
    private Boolean enableAutoCommit;
    private String autoOffsetReset;
    private String sessionTimeOut;
    private Integer autoCommitInterval;
    private String pollTimeOut;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Boolean getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(Boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public String getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(String sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public Integer getAutoCommitInterval() {
        return autoCommitInterval;
    }

    public void setAutoCommitInterval(Integer autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
    }

    public String getPollTimeOut() {
        return pollTimeOut;
    }

    public void setPollTimeOut(String pollTimeOut) {
        this.pollTimeOut = pollTimeOut;
    }
}
