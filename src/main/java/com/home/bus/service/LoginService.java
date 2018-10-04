package com.home.bus.service;

import com.home.bus.model.LoginResult;

/**
 * @Author: xu.dm
 * @Date: 2018/8/11 21:34
 * @Description:
 */
public interface LoginService {
    LoginResult login(String userName, String password);
    String getCurrentUserName();
    void logout();
}
