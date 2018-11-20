package com.home.bus.service;

import com.home.bus.entity.mq.MQMessage;

import java.util.List;

/**
 * @Author: xu.dm
 * @Date: 2018/11/20 16:14
 * @Description:
 */
public interface MQConsumerService {
    List<MQMessage> getMessage();
}
