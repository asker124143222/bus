package com.home.bus.repository;

import com.home.bus.entity.SysLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: xu.dm
 * @Date: 2018/10/11 18:43
 * @Description:
 */
public interface SysLogRepository extends JpaRepository<SysLog,Long>,JpaSpecificationExecutor<SysLog> {
    Page<SysLog> findAllByUserNameContains(String userName, Pageable pageable);
}
