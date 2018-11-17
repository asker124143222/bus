package com.home.bus.service.impl;

import com.home.bus.service.MQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class KafkaServiceImpl implements MQService {

    @Value("${spring.mqconfig.mq-enable}")
    private boolean mqEnable;

    @Value("${spring.kafka.topic.Name}")
    private String topicName;

    private Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Async("logThread")
    public void sendMessage(String msg) {
        if(!isMqEnable()) return;
        long start = System.currentTimeMillis();
        kafkaTemplate.send(topicName,msg);
        long end = System.currentTimeMillis();
        logger.info("写入kafka，耗时："+(end-start)+"毫秒");

    }

    public boolean isMqEnable() {
        return mqEnable;
    }

    public void setMqEnable(boolean mqEnable) {
        this.mqEnable = mqEnable;
    }
}
