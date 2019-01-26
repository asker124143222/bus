package com.home.bus.config;


import com.home.bus.entity.User;
import com.home.bus.model.ISysPermission;
import com.home.bus.model.IUserRole;
import com.home.bus.redis_shiro.RedisSessionDAO;
import com.home.bus.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

public class MyShiroRealm extends AuthorizingRealm {

    @Resource
//    @Lazy  //与springboot集成缓存开启@EnableCaching的时候，确保shiro的bean可以先实例化
    private UserService userService;

    //权限信息，包括角色以及权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限判断-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //如果授权部分没有传入User对象，这里只能取到userName
        //也就是SimpleAuthenticationInfo构造的时候第一个参数传递需要User对象
        User user = (User) principals.getPrimaryPrincipal();

        for (IUserRole role : user.getRoleList()) {
            authorizationInfo.addRole(role.getRole());
        }

        for (ISysPermission p : user.getPermissionList()) {
            authorizationInfo.addStringPermission(p.getPermission());
        }
//        for(SysRole role:user.getRoleList()){
//            authorizationInfo.addRole(role.getRole());
//            for(SysPermission p:role.getPermissions()){
//                authorizationInfo.addStringPermission(p.getPermission());
//            }
//        }
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        System.out.println("身份认证-->MyShiroRealm.doGetAuthenticationInfo()");
        //获取用户的输入的账号.
        String userName = (String) token.getPrincipal();
        if (userName == null)
            return null;
//        System.out.println(token.getCredentials());
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        String version = userService.getDbVersion();
        System.out.println(version);
        User user = userService.findByUserName(userName);
        System.out.println("-->>user=" + user);
        if (user == null) {
            return null;
        }
        user.setPermissionList(userService.findUserRolePermissionByUserName(user.getUserName()));
        user.setRoleList(userService.findUserRoleByUserName(user.getUserName()));
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, //这里传入的是user对象，比对的是用户名，直接传入用户名也没错，但是在授权部分就需要自己重新从数据库里取权限
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );

        return authenticationInfo;
    }

}