package com.home.bus.model;

/**
 * @Author: xu.dm
 * @Date: 2018/9/21 20:36
 * @Description:用于UserRepository自定义返回实体
 * 用户与角色对应表
 */
public interface IUserRole {
    Integer getUserId();
    String getUserName();
    Integer getRoleId();
    String getRole();
    String getDescription();
}
