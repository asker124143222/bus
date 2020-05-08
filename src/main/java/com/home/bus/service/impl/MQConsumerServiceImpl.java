package com.home.bus.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.bus.config.MQConfig;
import com.home.bus.entity.mq.MQMessage;
import com.home.bus.service.MQConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: xu.dm
 * @Date: 2018/11/20 16:15
 * @Description:
 */
@Service
@ConditionalOnProperty(prefix = "spring.mqconfig", name = "mq-enable" ,havingValue = "true")
public class MQConsumerServiceImpl implements MQConsumerService {
    private ObjectMapper objectMapper;
    private List<MQMessage> mqMessageList;
    private long maxMessageCount=100;

    @Resource
    private MQConfig mqConfig;

    @Override
    public List<MQMessage> getMessage() {

        if(mqMessageList==null) mqMessageList = new ArrayList<>();
        return mqMessageList;
    }

    @KafkaListener(topics = "${spring.kafka.topic.Name:null}")
    private void consumer(ConsumerRecord<?, ?> record)
    {
        if(mqMessageList==null) mqMessageList = new ArrayList<>();
        if(!mqConfig.isMqEnable()) return;
        if(objectMapper==null) objectMapper = new ObjectMapper();
        Optional<?> mqMessage = Optional.ofNullable(record.value());
        if (mqMessage.isPresent()) {
            Object message = mqMessage.get();
            try {
                if(mqMessageList.size()>maxMessageCount)
                {
                    mqMessageList.remove(0);
                }
                MQMessage mq =  objectMapper.readValue((String)message, MQMessage.class);
                mqMessageList.add(mq);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
