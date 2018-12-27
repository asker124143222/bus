package com.home.bus.service;

import com.home.bus.entity.esIndex.ESIndexObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ESIndexService {
    Page<ESIndexObject> getAllESIndex(Pageable pageable);
    Page<ESIndexObject> getESIndexByName(String indexName,Pageable pageable);
}
