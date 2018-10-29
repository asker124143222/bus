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
import java.util.Set;

/**
 * @Author: xu.dm
 * @Date: 2018/8/11 16:47
 * @Description:
 * 缓存设计ps：
 */
@Service
public class UserServiceImpl implements UserService {
    //key命名规则：前缀+":"+"实体ID或者实体name（userName）"+":业务（可选）"
    private String keyPrefix = "user:";

    @Resource
    private UserRepository userRepository;

    @Resource(name = "redisCacheServiceImpl")
    private CacheService cacheService;

    @Resource
    private CommonCacheConfig cacheConfig;

    private User findByUserNameInCache(String userName) {
        User user;
        String key = keyPrefix + userName;

        if (cacheService.hasKey(key)) {
            user = (User) cacheService.get(key);
            cacheService.expire(key, cacheConfig.getTimeToLive());
            System.out.println("----->>findByUserName read cache：" + key);
            return user;
        }

        user = userRepository.findByUserName(userName);
        System.out.println("----->>findByUserName write cache：" + key);
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

    //代理实现的接口类，暂时没有找到合适的反序列化方式
    private List<ISysPermission> findUserRolePermissionInCache(String userName) {
        String key = this.keyPrefix + userName+":UserRolePermission";
        List<ISysPermission> sysPermissions;
        if (cacheService.hasKey(key)) {
            System.out.println("----->>UserRolePermission read cache：" + key);
            sysPermissions = (List<ISysPermission>) cacheService.get(key);
            cacheService.expire(key, cacheConfig.getTimeToLive());
            return sysPermissions;
        }
        sysPermissions = userRepository.findUserRolePermissionByUserName(userName);
        System.out.println("----->>UserRolePermission write cache：" + key);
        cacheService.set(key, sysPermissions, cacheConfig.getTimeToLive());
        return sysPermissions;
    }

    @Override
    public List<ISysPermission> findUserRolePermissionByUserName(String userName) {
        //反序列化有问题，先注销
//        if (cacheConfig.isCacheEnable())
//            return findUserRolePermissionInCache(userName);

        return userRepository.findUserRolePermissionByUserName(userName);
    }

    private User findUserInCatch(Integer userId)
    {
        String key = this.keyPrefix+userId;
        User user;
        if(cacheService.hasKey(key))
        {
            System.out.println("----->>findUserById read cache：" + key);
            user = (User)cacheService.get(key);
            cacheService.expire(key,cacheConfig.getTimeToLive());
            return user;
        }

        System.out.println("----->>findUserById write cache：" + key);
        user = userRepository.findById(userId).orElse(null);
        cacheService.set(key,user,cacheConfig.getTimeToLive());
        return user;
    }

    @Override
    public User findUserById(Integer userId) {
        if(cacheConfig.isCacheEnable())
            return findUserInCatch(userId);
        return userRepository.findById(userId).orElse(null);
    }

    //保存和修改user对象的时候删除对应user的缓存
    private void deleteCache(User user) {

        if(user==null)
            return;

        String key = this.keyPrefix + user.getUserName();

        Set<String> keys = null;
        keys = cacheService.getKey(key+"*");
        if(keys!=null && keys.size()>0)
        {
            System.out.println("----->>delete cache：key:" + keys);
            cacheService.del(keys);
        }

        key = this.keyPrefix + user.getUserId();
        keys = cacheService.getKey(key + "*");
        if (keys != null && keys.size() > 0) {
            System.out.println("----->>delete cache：key:" + keys);
            cacheService.del(keys);
        }


        //缺省查询状态下userName=空字符
        key = this.keyPrefix + ":";
        keys = cacheService.getKey(key+"*");
        if(keys!=null && keys.size()>0)
        {
            System.out.println("----->>delete cache：key:" + keys);
            cacheService.del(keys);
        }
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
        Page<User> userPage;
        if (cacheService.hasKey(key)) {
            userPage = (Page<User>) cacheService.get(key);
            cacheService.expire(key, cacheConfig.getTimeToLive());
            System.out.println("----->>findAllByUserNameContains read cache：" + key);
            return userPage;
        }

        userPage = userRepository.findAllByUserNameContains(userName, pageable);
        cacheService.set(key, userPage, cacheConfig.getTimeToLive());
        System.out.println("----->>findAllByUserNameContains write cache：" + key);

        return userPage;

    }

    @Override
    public Page<User> findAllByUserNameContains(String userName, Pageable pageable) {
        if (cacheConfig.isCacheEnable())
            return findAllByUserNameContainsInCache(userName, pageable);
        return userRepository.findAllByUserNameContains(userName, pageable);
    }

    //按id判断是否有user缓存
    private User checkCacheByUserId(Integer userId)
    {
        User user = null;
        String key = this.keyPrefix + userId;
        if(cacheService.hasKey(key))
        {
            user = (User)cacheService.get(key);
        }

        return user;
    }

    //删除和userid关联对象的所有缓存
    private void deleteCacheByUserId(List<Integer> userIdList)
    {
        User user;

        Set<String>  keys;
        String key;
        for(Integer userId:userIdList)
        {
             user = checkCacheByUserId(userId);
             if(user!=null)
             {
                key = this.keyPrefix+user.getUserId();
                keys = cacheService.getKey(key+"*");
                if(keys!=null && keys.size()>0) {
                    System.out.println("----->>delete cache：key:" + keys);
                    cacheService.del(keys);
                }

                 key = this.keyPrefix+user.getUserName();
                 keys = cacheService.getKey(key+"*");
                 if(keys!=null && keys.size()>0) {
                     System.out.println("----->>delete cache：key:" + keys);
                     cacheService.del(keys);
                 }

                 //删除userName或者userId为空的数据
                 key = this.keyPrefix+":";
                 keys = cacheService.getKey(key+"*");
                 if(keys!=null && keys.size()>0) {
                     System.out.println("----->>delete cache：key:" + keys);
                     cacheService.del(keys);
                 }
             }
        }
    }

    @Transactional
    @Override
    public void deleteAllUserByUserIdList(List<Integer> userIdList) {
        try {
            if(cacheConfig.isCacheEnable())
                deleteCacheByUserId(userIdList);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        userRepository.deleteAllUserRoleByUserIdList(userIdList);
        userRepository.deleteAllUserByUserIdList(userIdList);
    }

    @Transactional
    @Override
    public void deleteAllUserRoleByUserIdList(List<Integer> userIdList) {
        try {
            if(cacheConfig.isCacheEnable())
                deleteCacheByUserId(userIdList);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        userRepository.deleteAllUserRoleByUserIdList(userIdList);
    }

    @Transactional
    @Override
    public void deleteAllUserRoleByUserId(Integer userId) {
        try {
            if(cacheConfig.isCacheEnable()) {
                List<Integer> userIdList = new ArrayList<>();
                userIdList.add(userId);
                deleteCacheByUserId(userIdList);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        userRepository.deleteAllUserRoleByUserId(userId);
    }

    @Transactional
    @Override
    public void grantUserRole(Integer userId, List<Integer> roleIdList) {
//        try {
//            if(cacheConfig.isCacheEnable()) {
//                List<Integer> userIdList = new ArrayList<>();
//                userIdList.add(userId);
//                deleteCacheByUserId(userIdList);
//            }
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        userRepository.deleteAllUserRoleByUserId(userId);
        deleteAllUserRoleByUserId(userId);
        for (Integer roleId : roleIdList) {
            userRepository.insertUserRole(userId, roleId);
        }
    }

}
