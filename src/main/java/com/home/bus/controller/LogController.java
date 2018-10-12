package com.home.bus.controller;

import com.home.bus.entity.SysLog;
import com.home.bus.service.LogService;
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
import java.util.Map;

/**
 * @Author: xu.dm
 * @Date: 2018/10/12 16:07
 * @Description:
 */
@Controller
@RequestMapping(value = "/log")
public class LogController {
    @Resource
    LogService logService;

    @RequestMapping(value="/list")
    public String list()
    {
        return "/user/logList";
    }

    @RequestMapping(value = "/getLogList")
    @ResponseBody
    public Object getLogList(HttpServletRequest request)
    {
        int pageSize = 10;
        try {
            pageSize =  Integer.parseInt(request.getParameter("pageSize"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        int pageNumber=0 ;
        try {
            pageNumber =  Integer.parseInt(request.getParameter("pageNumber"))-1;
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();

        String searchText=request.getParameter("searchText")==null ? "": request.getParameter("searchText");

        String sortName=request.getParameter("sortName")==null ? "roleId": request.getParameter("sortName");
        String sortOrder=request.getParameter("sortOrder")==null ? "asc": request.getParameter("sortOrder");

        Sort sortLocal = new Sort(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC: Sort.Direction.DESC,sortName);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortLocal);
//        Page<SysLog> sysLogPage = logService.findAllByUserNameContains(searchText,pageable);
        Page<SysLog> sysLogPage = logService.findAll(searchText,pageable);
        map.put("total",sysLogPage.getTotalElements());
        map.put("rows",sysLogPage.getContent());

        return map;
    }
}
