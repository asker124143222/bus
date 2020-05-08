package com.home.bus.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.bus.config.MQConfig;
import com.home.bus.entity.mq.MQMessage;
import com.home.bus.service.MQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: xu.dm
 * @Date: 2018/11/17 18:24
 * @Description:
 */
@Service
@ConditionalOnProperty(prefix = "spring.mqconfig", name = "mq-enable" ,havingValue = "true")
public class KafkaServiceImpl implements MQService {

    @Resource
    private MQConfig mqConfig;

    private Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Async("logThread")
    public void sendMessage(String msg) {
        if(!mqConfig.isMqEnable()) return;
        long start = System.currentTimeMillis();
        kafkaTemplate.send(mqConfig.getTopicName(),msg);
        long end = System.currentTimeMillis();
        logger.info("写入kafka，耗时："+(end-start)+"毫秒");
    }
}
