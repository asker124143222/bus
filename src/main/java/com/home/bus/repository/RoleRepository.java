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

    @Query(value="select * from SysRole where role <> ?1 and role = ?2",nativeQuery = true)
    SysRole findSysRoleExists2(String oldRole, String newRole);

    @Modifying
    @Query(value = "delete from SysRole where roleId in (?1)",nativeQuery = true)
    void deleteAllByRoleIdList(List<Integer> roleIdList);

    @Query(value="select a.permissionId,a.permission,a.permissionName,a.parentId,c.roleId,c.role  from syspermission a \n" +
            "left join sysrolepermission b on a.permissionId=b.permissionId and a.available=1 and b.roleId=?1\n" +
            "left join sysrole c on c.roleId=b.roleId",
    countQuery = "select count(*)  from syspermission a \n" +
            "left join sysrolepermission b on a.permissionId=b.permissionId and a.available=1 and b.roleId=?1\n" +
            "left join sysrole c on c.roleId=b.roleId",
    nativeQuery = true)
    List<ISysRolePermission> findSysRolePermissionByRoleId(Integer roleId);


    @Modifying
    @Query(value = "delete from SysRolePermission where roleId=?1",nativeQuery = true)
    void deleteRolePermission(Integer roleId);

    @Modifying
    @Query(value="insert into SysRolePermission(roleId,permissionId) VALUES(?1,?2) ",nativeQuery = true)
    void insertRolePermission(Integer roleId, Integer permissionId);
}
