package com.home.bus.service.impl;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.bus.config.CommonCacheConfig;
import com.home.bus.entity.User;
import com.home.bus.model.ISysPermission;
import com.home.bus.model.IUserRole;
import com.home.bus.repository.UserRepository;
import com.home.bus.service.CacheService;
import com.home.bus.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: xu.dm
 * @Date: 2018/8/11 16:47
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {
    private String keyPrefix = "user:";//冒号分隔

    @Resource
    private UserRepository userRepository;

    @Resource(name = "redisCacheServiceImpl")
    private CacheService cacheService;

    @Resource
    private CommonCacheConfig cacheConfig;

    private User findByUserNameInCache(String userName) {
        User user = null;
        String key = keyPrefix + userName;

        if (cacheService.hasKey(key)) {
            user = (User) cacheService.get(key);
            cacheService.expire(key, cacheConfig.getTimeToLive());
            System.out.println("----->>findByUserName 读取缓存：" + key);
            return user;
        }

        user = userRepository.findByUserName(userName);
        System.out.println("----->>findByUserName 写入缓存：" + key);
        cacheService.set(key, user, cacheConfig.getTimeToLive());

        return user;
    }

    @Override
    public User findByUserName(String userName) {
        if (cacheConfig.isCacheEnable())
            return findByUserNameInCache(userName);
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<IUserRole> findUserRoleByUserName(String userName) {
        return userRepository.findUserRoleByUserName(userName);
    }

    @Override
    public List<IUserRole> findAllUserRoleByUserId(Integer userId) {
        return userRepository.findAllUserRoleByUserId(userId);
    }

    private List<ISysPermission> findUserRolePermissionInCache(String userName) {
        String key = this.keyPrefix + "UserRolePermission:" + userName;
        List<ISysPermission> sysPermissions = null;
        if (cacheService.hasKey(key)) {
            System.out.println("----->>UserRolePermission 读取缓存：" + key);
            sysPermissions = (List<ISysPermission>) cacheService.get(key);
            cacheService.expire(key, cacheConfig.getTimeToLive());
            return sysPermissions;
        }
        sysPermissions = userRepository.findUserRolePermissionByUserName(userName);
        System.out.println("----->>UserRolePermission 写入缓存：" + key);
        cacheService.set(key, sysPermissions, cacheConfig.getTimeToLive());
        return sysPermissions;
    }

    @Override
    public List<ISysPermission> findUserRolePermissionByUserName(String userName) {
        //反序列化有问题，先注销
        if (cacheConfig.isCacheEnable())
            return findUserRolePermissionInCache(userName);

        return userRepository.findUserRolePermissionByUserName(userName);
    }

    @Override
    public Optional<User> findUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    private void deleteCache(User user) {


        String key1 = this.keyPrefix + user.getUserName();
        String key2 = this.keyPrefix + user.getUserId();
        String key3 = this.keyPrefix + "UserRolePermission:" + user.getUserName();

        System.out.println("----->>save 删除缓存：key1:" + key1 + ",key2:" + key2);
        cacheService.del(key1, key2,key3);
    }

    @Override
    public User save(User user) {

        User u = userRepository.save(user);
        try {
            if(cacheConfig.isCacheEnable())
                deleteCache(u);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public boolean checkUserExists(String userName) {
        User user = this.findByUserName(userName);
        if (user != null)
            return true;
        else
            return false;
    }

    @Override
    public boolean checkUserExists2(String oldUserName, String newUserName) {
        User user = userRepository.findUserExist2(oldUserName, newUserName);

        if (user != null)
            return true;
        else
            return false;
    }

    private Page<User> findAllByUserNameContainsInCache(String userName, Pageable pageable) {

        String key = this.keyPrefix + userName + ":" + pageable.toString();
        Page<User> userPage = null;
        if (cacheService.hasKey(key)) {
            userPage = (Page<User>) cacheService.get(key);
            cacheService.expire(key, cacheConfig.getTimeToLive());
            System.out.println("----->>findAllByUserNameContains 读取缓存：" + key);
            return userPage;
        }

        userPage = userRepository.findAllByUserNameContains(userName, pageable);
        cacheService.set(key, userPage, cacheConfig.getTimeToLive());
        System.out.println("----->>findAllByUserNameContains 写入缓存：" + key);

        return userPage;

    }

    @Override
    public Page<User> findAllByUserNameContains(String userName, Pageable pageable) {
        if (cacheConfig.isCacheEnable())
            return findAllByUserNameContainsInCache(userName, pageable);
        return userRepository.findAllByUserNameContains(userName, pageable);
    }

    @Transactional
    @Override
    public void deleteAllUserByUserIdList(List<Integer> userIdList) {
        userRepository.deleteAllUserRoleByUserIdList(userIdList);
        userRepository.deleteAllUserByUserIdList(userIdList);
    }

    @Transactional
    @Override
    public void deleteAllUserRoleByUserIdList(List<Integer> userIdList) {
        userRepository.deleteAllUserRoleByUserIdList(userIdList);
    }

    @Transactional
    @Override
    public void deleteAllUserRoleByUserId(Integer userId) {
        userRepository.deleteAllUserRoleByUserId(userId);
    }

    @Transactional
    @Override
    public void grantUserRole(Integer userId, List<Integer> roleIdList) {
        userRepository.deleteAllUserRoleByUserId(userId);
        for (Integer roleId : roleIdList) {
            userRepository.insertUserRole(userId, roleId);
        }
    }

}
