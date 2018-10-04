package com.home.bus.model;

/**
 * @Author: xu.dm
 * @Date: 2018/9/21 20:37
 * @Description:用于UserRepository自定义返回实体
 * 用户与权限对应表
 */
public interface ISysPermission {
    Integer getUserId();
    String getUserName();
    Integer getPermissionId();
    String getPermission();
    String getPermissionName();
}
