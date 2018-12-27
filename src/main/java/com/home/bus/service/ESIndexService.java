package com.home.bus.service;

import com.home.bus.entity.esIndex.ESIndexObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ESIndexService {
    List<ESIndexObject> getAllESIndex();
    List<ESIndexObject> getESIndexByName(String index);
    Page<ESIndexObject> getAllESIndex(Pageable pageable);
    Page<ESIndexObject> getESIndexByName(String index,Pageable pageable);
}
