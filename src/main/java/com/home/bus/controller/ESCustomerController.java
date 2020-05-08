package com.home.bus.controller;

import com.home.bus.entity.esIndex.ESCustomer;
import com.home.bus.service.ESCustomerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xu.dm
 * @Date: 2018/12/29 12:12
 * @Description:
 */

@Controller
@ConditionalOnProperty(prefix = "elasticsearch",name = "enable",havingValue = "true")
@RequestMapping("/esCustomer")
public class ESCustomerController {
    @Resource
    ESCustomerService esCustomerService;

    @RequestMapping("/list")
    public String list() {
        return "/ESCustomer/customerList";
    }

    @RequestMapping("/getList")
    @ResponseBody
    public Object getESCustomerList(HttpServletRequest request) {

        Pageable pageable = ControllerUtils.getPageInfo(request);
        String searchText = ControllerUtils.getSearchText(request);
        Page<ESCustomer> page = esCustomerService.searchAllByAllField(searchText,pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("total", page.getTotalElements());
        map.put("rows", page.getContent());

        return map;
    }
}
