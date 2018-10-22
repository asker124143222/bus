package com.home.bus.model;

import java.io.Serializable;

/**
 * @Author: xu.dm
 * @Date: 2018/9/22 21:27
 * @Description:角色权限对照表，列出全部权限，没有权限的role和roleId为null
 */
public interface ISysRolePermission extends Serializable {
    Integer getRoleId();
    String getRole();
    Integer getPermissionId();
    String getPermission();
    String getPermissionName();
    Integer getParentId();

}
