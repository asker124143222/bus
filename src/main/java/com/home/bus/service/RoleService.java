package com.home.bus.service;

import com.home.bus.entity.SysRole;
import com.home.bus.model.ISysRolePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @Author: xu.dm
 * @Date: 2018/9/5 22:09
 * @Description:
 */
public interface RoleService {
    Page<SysRole> findAll(Pageable pageable);

    Page<SysRole> findAllByRoleContains(String role, Pageable pageable);

    Optional<SysRole> findById(Integer roleId);

    SysRole save(SysRole sysRole);

    boolean checkRoleExists(String role);

    boolean checkRoleExists(String oldRole, String newRole);

    boolean deleteAllByRoleIdIn(List<Integer> roleIdList);

    List<ISysRolePermission> findSysRolePermissionByRoleId(Integer roleId);

    void grantAuthorization(Integer roleId, List<Integer> permissionList);

    void clearAuthorization(Integer roleId);
}
