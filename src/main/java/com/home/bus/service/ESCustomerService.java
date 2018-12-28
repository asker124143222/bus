package com.home.bus.service;

import com.home.bus.entity.esIndex.ESCustomer;

import java.util.List;

/**
 * @Author: xu.dm
 * @Date: 2018/12/26 16:33
 * @Description:
 */
public interface ESCustomerService {
    List<ESCustomer> searchByName(String name);
}
