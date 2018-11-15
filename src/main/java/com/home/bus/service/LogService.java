package com.home.bus.service;

import com.home.bus.entity.SysLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @Author: xu.dm
 * @Date: 2018/10/11 0:01
 * @Description:
 */
public interface LogService {
    void writeLog(SysLog sysLog);
    Page<SysLog> findAll(Pageable pageable);
    Page<SysLog> findAllByUserNameContains(String userName, Pageable pageable);
    Page<SysLog> findAll(String searchText, Pageable pageable);
}
