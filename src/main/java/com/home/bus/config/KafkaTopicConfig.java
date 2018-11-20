package com.home.bus.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xu.dm
 * @Date: 2018/11/17 22:58
 * @Description:
 */
@Configuration
@EnableKafka
public class KafkaTopicConfig {

    @Value("${spring.mqconfig.mq-enable}")
    private boolean mqEnable;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value("${spring.kafka.topic.Name}")
    private String topicName;

    @Value("${spring.kafka.topic.numPartitions}")
    private int numPartitions;

    @Value("${spring.kafka.topic.replicationFactor}")
    private int replicationFactor;
    @Bean
    public KafkaAdmin kafkaAdmin() {
        if(!mqEnable) return null;
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic myTopic() {
        if(!mqEnable) return null;
        //第三个参数是副本数量，确保集群中配置的数目大于等于副本数量
        return new NewTopic(topicName, numPartitions, (short) replicationFactor);
    }

}
