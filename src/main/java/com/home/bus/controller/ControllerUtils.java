package com.home.bus.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;

public class ControllerUtils {
    public static Pageable getPageInfo(HttpServletRequest request)
    {
        if(request==null) return null;
        int pageSize = 10;
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


        String sortName = request.getParameter("sortName") == null ? "" : request.getParameter("sortName");
        String sortOrder = request.getParameter("sortOrder") == null ? "asc" : request.getParameter("sortOrder");

        Sort sortLocal = new Sort(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortName);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortLocal);

        return pageable;
    }

    public static String getSearchText(HttpServletRequest request)
    {
        if (request==null) return "";
        return request.getParameter("searchText") == null ? "" : request.getParameter("searchText");
    }
}
