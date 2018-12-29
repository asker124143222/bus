package com.home.bus.service;

import com.home.bus.entity.esIndex.ESCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Author: xu.dm
 * @Date: 2018/12/26 16:33
 * @Description:
 */
public interface ESCustomerService {
    List<ESCustomer> searchAll();
    Page<ESCustomer> searchAllInPage(Pageable pageable);
    Page<ESCustomer> searchAllByMultiMatch(String query,Pageable pageable);

}
