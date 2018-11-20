package com.home.bus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.bus.entity.SysLog;
import com.home.bus.entity.mq.MQMessage;
import com.home.bus.service.LogService;
import com.home.bus.service.MQConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: xu.dm
 * @Date: 2018/10/12 16:07
 * @Description:
 */
@Controller
@RequestMapping(value = "/mq")
public class mqController {

    @Resource(name = "MQConsumerServiceImpl")
    private MQConsumerService mqConsumerService;

    @RequestMapping(value = "/list")
    public String list() {
        return "/mq/mqList";
    }

    @RequestMapping(value = "/getMsgList")
    @ResponseBody
    public Object getMsgList(HttpServletRequest request) {

        int pageSize = 50;
        try {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int pageNumber = 0;
        try {
            pageNumber = Integer.parseInt(request.getParameter("pageNumber")) - 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();

        String searchText = request.getParameter("searchText") == null ? "" : request.getParameter("searchText");

        String sortName = request.getParameter("sortName") == null ? "roleId" : request.getParameter("sortName");
        String sortOrder = request.getParameter("sortOrder") == null ? "asc" : request.getParameter("sortOrder");

        Sort sortLocal = new Sort(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortName);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortLocal);

        Page<MQMessage> mqMessagePage = new PageImpl<MQMessage>(mqConsumerService.getMessage(),pageable,this.mqConsumerService.getMessage().size());
        map.put("total", mqMessagePage.getTotalElements());
        map.put("rows", mqMessagePage.getContent());

        return map;
    }


}
