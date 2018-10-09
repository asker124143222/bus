package com.home.bus.repository;


import com.home.bus.entity.User;
import com.home.bus.model.ISysPermission;
import com.home.bus.model.IUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Author: xu.dm
 * @Date: 2018/6/10 19:54
 * @Description:
 */

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUserName(String userName);
    Page<User> findAllByUserNameContains(String userName, Pageable pageable);

    //排除现有用户的情况下，判断新用户是否存在
    @Query(value="select * from user where userName<> ?1 and userName=?2",nativeQuery = true)
    User findUserExist2(String oldUserName, String newUserName);

    //根据userid列表删除所有用户
    @Modifying
    @Query(value="delete from user where userId in (?1)",nativeQuery = true)
    void deleteAllUserByUserIdList(List<Integer> userIdList);

    //根据userid删除用户角色关联表里的记录
    @Modifying
    @Query(value="delete from SysUserRole where userId in (?1)",nativeQuery = true)
    void deleteAllUserRoleByUserIdList(List<Integer> userIdList);

    //根据userid删除用户角色关联表里的记录
    @Modifying
    @Query(value="delete from SysUserRole where userId = ?1",nativeQuery = true)
    void deleteAllUserRoleByUserId(Integer userId);

    //新增用户和角色关联记录
    @Modifying
    @Query(value="insert into SysUserRole(userId,roleId) VALUES(?1,?2)",nativeQuery = true)
    void insertUserRole(Integer userId, Integer roleId);

    //根据用户名获取用户所具备的角色列表
    @Query(value="select a.userId,a.userName,c.roleId,c.role,c.description from user a\n" +
            "inner join sysuserrole b on a.userId = b.userId \n" +
            "inner join sysrole c on b.roleId=c.roleId and c.available=1\n" +
            "where a.userName=?1",
    countQuery = "select count(*) from user a\n" +
            "inner join sysuserrole b on a.userId = b.userId \n" +
            "inner join sysrole c on b.roleId=c.roleId and c.available=1\n" +
            "where a.userName=?1",
    nativeQuery = true)
    List<IUserRole> findUserRoleByUserName(String userName);

    //根据用户id，列出所有角色，包括该用户不具备的角色，该用户不具备角色的时候，userid和username为null，可以做业务判断
    @Query(value="select a.roleId,a.role,a.description,c.userId,c.userName from SysRole a\n" +
            "left join SysUserRole b on a.roleId=b.roleId and a.available=1 and b.userId=?1\n" +
            "left join user c on c.userId=b.userId;",
    countQuery = "select count(*) from SysRole a\n" +
            "left join SysUserRole b on a.roleId=b.roleId and a.available=1 and b.userId=?1\n" +
            "left join user c on c.userId=b.userId;",
    nativeQuery = true)
    List<IUserRole> findAllUserRoleByUserId(Integer userId);

    //根据用户名，获取用户具备的权限。
    @Query(value="select a.userId,a.userName,d.permissionId,d.permission,d.permissionName from user a \n" +
            "inner join sysuserrole b on a.userId = b.userId \n" +
            "inner join sysrolepermission c on b.roleId = c.roleId\n" +
            "inner join syspermission d on c.permissionId=d.permissionId\n" +
            "where a.userName=?1",
            countQuery = "select a.userId,a.userName,d.permissionId,d.permission,d.permissionName from user a \n" +
                    "inner join sysuserrole b on a.userId = b.userId \n" +
                    "inner join sysrolepermission c on b.roleId = c.roleId\n" +
                    "inner join syspermission d on c.permissionId=d.permissionId\n" +
                    "where a.userName=?1",
            nativeQuery = true)
    List<ISysPermission> findUserRolePermissionByUserName(String userName);

}
