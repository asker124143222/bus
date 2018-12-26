package com.home.bus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: xu.dm
 * @Date: 2018/12/26 18:41
 * @Description:
 */
@Controller
@RequestMapping(value = "/es")
public class ESController {
    @RequestMapping(value = "/list")
    public String list() {
        return "/ESIndex/indexList";
    }
}
