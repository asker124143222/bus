package com.home.bus.service;

import com.home.bus.entity.mq.MQMessage;

/**
 * @Author: xu.dm
 * @Date: 2018/11/17 18:21
 * @Description:
 */
public interface MQService {
    void sendMessage(String msg);
}
