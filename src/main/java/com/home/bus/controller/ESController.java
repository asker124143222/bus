package com.home.bus.controller;

import com.home.bus.entity.esIndex.ESIndexObject;
import com.home.bus.service.ESIndexService;
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
 * @Date: 2018/12/26 18:41
 * @Description:
 */
@Controller
@RequestMapping(value = "/es")
public class ESController {

    @Resource
    ESIndexService esIndexService;

    @RequestMapping(value = "/list")
    public String list() {
        return "/ESIndex/indexList";
    }

    @RequestMapping(value = "/getIndexList")
    @ResponseBody
    public Object getESIndexList(HttpServletRequest request)
    {
//        无法有效的在server端分页，把分页挪到client端
//        int pageSize = 10;
//        try {
//            pageSize = Integer.parseInt(request.getParameter("pageSize"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        int pageNumber = 0;
//        try {
//            pageNumber = Integer.parseInt(request.getParameter("pageNumber")) - 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String searchText = request.getParameter("searchText") == null ? "" : request.getParameter("searchText");
//        String sortName = request.getParameter("sortName") == null ? "roleId" : request.getParameter("sortName");
//        String sortOrder = request.getParameter("sortOrder") == null ? "asc" : request.getParameter("sortOrder");
//
//        Sort sortLocal = new Sort(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortName);
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortLocal);
//
//        Map<String, Object> map = new HashMap<>();
//        Page<ESIndexObject> page = esIndexService.getAllESIndex(pageable);
//
//        map.put("total", page.getTotalElements());
//        map.put("rows", page.getContent());

//        return map;
//        List<ESIndexObject> list = esIndexService.getAllESIndex();
        List<ESIndexObject> list = esIndexService.getESIndexByName(searchText);
        return list;
    }
}
