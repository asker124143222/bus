package com.home.bus.factory;

import com.home.bus.entity.SysLog;
import com.home.bus.utils.NetworkUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @Author: xu.dm
 * @Date: 2018/11/15 23:52
 * @Description:
 */
public class LogFactory {
    public static SysLog createSysLog(String action,String event)
    {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SysLog sysLog = new SysLog();
        sysLog.setAction(action);
        sysLog.setEvent(event);
        sysLog.setHost(NetworkUtils.getIpAddress(request));
        sysLog.setUserName((String)request.getSession().getAttribute("userName"));
        sysLog.setInsertTime(LocalDateTime.now());

        return sysLog;
    }
}
