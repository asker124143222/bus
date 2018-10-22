package com.home.bus.service;


import com.home.bus.entity.User;
import com.home.bus.model.ISysPermission;
import com.home.bus.model.IUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @Author: xu.dm
 * @Date: 2018/8/11 16:45
 * @Description:
 */
public interface UserService {
    User findByUserName(String userName);

    Optional<User> findUserById(Integer userId);

    User save(User user);

    //新增用户判断是否有重名的
    boolean checkUserExists(String userName);
    //修改用户判断是否有重名的，不包括即将被修改的原名
    boolean checkUserExists2(String oldUserName, String newUserName);

    List<IUserRole> findUserRoleByUserName(String userName);

    List<IUserRole> findAllUserRoleByUserId(Integer userId);

    List<ISysPermission> findUserRolePermissionByUserName(String userName);

    Page<User> findAllByUserNameContains(String userName, Pageable pageable);

    void deleteAllUserByUserIdList(List<Integer> userIdList);

    void deleteAllUserRoleByUserIdList(List<Integer> userIdList);

    void deleteAllUserRoleByUserId(Integer userId);

    void grantUserRole(Integer userId, List<Integer> roleIdList);
    
}
