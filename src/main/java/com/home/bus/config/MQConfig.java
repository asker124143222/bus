package com.home.bus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xu.dm
 * @Date: 2018/11/21 22:36
 * @Description:
 */
@Configuration
public class MQConfig {
    @Value("${spring.mqconfig.mq-enable:false}")
    private boolean mqEnable;

    @Value("${spring.kafka.bootstrap-servers:localhost}")
    private String bootstrapServer;

    @Value("${spring.kafka.topic.Name:default}")
    private String topicName;

    @Value("${spring.kafka.topic.numPartitions:1}")
    private int numPartitions;

    @Value("${spring.kafka.topic.replicationFactor:1}")
    private int replicationFactor;

    public boolean isMqEnable() {
        return mqEnable;
    }

    public void setMqEnable(boolean mqEnable) {
        this.mqEnable = mqEnable;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getNumPartitions() {
        return numPartitions;
    }

    public void setNumPartitions(int numPartitions) {
        this.numPartitions = numPartitions;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
}
