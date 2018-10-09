package com.home.bus.repository;

import com.home.bus.entity.SysRole;
import com.home.bus.model.ISysRolePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * @Author: xu.dm
 * @Date: 2018/9/5 21:57
 * @Description:
 */
public interface RoleRepository extends JpaRepository<SysRole,Integer> {
    Page<SysRole> findAllByRoleContains(String role, Pageable pageable);
    SysRole findSysRoleByRole(String role);

    //排除现有角色的情况下，判断新的角色是否存在
    @Query(value="select * from SysRole where role <> ?1 and role = ?2",nativeQuery = true)
    SysRole findSysRoleExists2(String oldRole, String newRole);

    //根据roleid列表删除所有的角色
    @Modifying
    @Query(value = "delete from SysRole where roleId in (?1)",nativeQuery = true)
    void deleteAllByRoleIdList(List<Integer> roleIdList);

    //根据roleid列出所有权限，包括没有的权限，如果没有权限，则roleid和role两个字段会是null值，可以根据此做业务判断
    @Query(value="select a.permissionId,a.permission,a.permissionName,a.parentId,c.roleId,c.role  from syspermission a \n" +
            "left join sysrolepermission b on a.permissionId=b.permissionId and a.available=1 and b.roleId=?1\n" +
            "left join sysrole c on c.roleId=b.roleId",
    countQuery = "select count(*)  from syspermission a \n" +
            "left join sysrolepermission b on a.permissionId=b.permissionId and a.available=1 and b.roleId=?1\n" +
            "left join sysrole c on c.roleId=b.roleId",
    nativeQuery = true)
    List<ISysRolePermission> findSysRolePermissionByRoleId(Integer roleId);

    //根据roleid删除角色权限关联表里所有角色权限
    @Modifying
    @Query(value = "delete from SysRolePermission where roleId=?1",nativeQuery = true)
    void deleteRolePermission(Integer roleId);

    //插入角色和权限
    @Modifying
    @Query(value="insert into SysRolePermission(roleId,permissionId) VALUES(?1,?2) ",nativeQuery = true)
    void insertRolePermission(Integer roleId, Integer permissionId);
}
