package com.home.bus.service.impl;

import com.home.bus.entity.SysLog;
import com.home.bus.repository.SysLogRepository;
import com.home.bus.service.LogService;
import com.home.bus.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xu.dm
 * @Date: 2018/10/11 18:42
 * @Description:
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    SysLogRepository sysLogRepository;

    private Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    @Async("logThread")
    public void writeLog(SysLog sysLog)
    {
        long start = System.currentTimeMillis();
//        try {
//            Thread.sleep(3000);
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
        sysLogRepository.save(sysLog);
        long end = System.currentTimeMillis();
        logger.info("异步日志入库完成，耗时："+(end-start)+"毫秒，入库内容："+sysLog);
    }

    @Override
    public Page<SysLog> findAll(Pageable pageable) {
        return sysLogRepository.findAll(pageable);
    }

    @Override
    public Page<SysLog> findAllByUserNameContains(String userName, Pageable pageable) {
        return sysLogRepository.findAllByUserNameContains(userName,pageable);
    }

    @Override
    public Page<SysLog> findAll(String searchText, Pageable pageable) {
        return sysLogRepository.findAll(new Specification<SysLog>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<SysLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                Predicate predicate = criteriaBuilder.conjunction();
                List<Predicate> predicates = new ArrayList<>();
                if(searchText!=null && !searchText.isEmpty())
                {
                    predicates.add(criteriaBuilder.like(root.get("userName"),"%"+searchText+"%"));
                    predicates.add(criteriaBuilder.like(root.get("action"),"%"+searchText+"%"));
                    predicates.add(criteriaBuilder.like(root.get("event"),"%"+searchText+"%"));
                    predicates.add(criteriaBuilder.like(root.get("host"),"%"+searchText+"%"));
                    return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
                }
                else
                    return null;

            }
        },pageable);
    }
}
