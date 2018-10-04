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

    @Query(value="select * from user where userName<> ?1 and userName=?2",nativeQuery = true)
    User findUserExist2(String oldUserName, String newUserName);

    @Modifying
    @Query(value="delete from user where userId in (?1)",nativeQuery = true)
    void deleteAllUserByUserIdList(List<Integer> userIdList);

    @Modifying
    @Query(value="delete from SysUserRole where userId in (?1)",nativeQuery = true)
    void deleteAllUserRoleByUserIdList(List<Integer> userIdList);

    @Modifying
    @Query(value="delete from SysUserRole where userId = ?1",nativeQuery = true)
    void deleteAllUserRoleByUserId(Integer userId);

    @Modifying
    @Query(value="insert into SysUserRole(userId,roleId) VALUES(?1,?2)",nativeQuery = true)
    void insertUserRole(Integer userId, Integer roleId);

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

    @Query(value="select a.roleId,a.role,a.description,c.userId,c.userName from SysRole a\n" +
            "left join SysUserRole b on a.roleId=b.roleId and a.available=1 and b.userId=?1\n" +
            "left join user c on c.userId=b.userId;",
    countQuery = "select count(*) from SysRole a\n" +
            "left join SysUserRole b on a.roleId=b.roleId and a.available=1 and b.userId=?1\n" +
            "left join user c on c.userId=b.userId;",
    nativeQuery = true)
    List<IUserRole> findAllUserRoleByUserId(Integer userId);

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
