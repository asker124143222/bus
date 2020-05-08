package com.home.bus.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.bus.entity.esIndex.ESIndexObject;
import com.home.bus.service.ESIndexService;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "elasticsearch",name = "enable",havingValue = "true")
public class ESIndexServiceImpl implements ESIndexService {

    @Resource
    RestClient restClient;

    @Override
    public List<ESIndexObject> getAllESIndex() {
        return getESIndexByName("");
    }

    private <T> List<T> getDataByQuery(String method,String query)
    {
        Request request = new Request(method.toUpperCase(),query);
        try {
            Response response = restClient.performRequest(request);
            RequestLine requestLine = response.getRequestLine();
            HttpHost host = response.getHost();
            int statusCode = response.getStatusLine().getStatusCode();
            Header[] headers = response.getHeaders();
            System.out.println(requestLine);
            System.out.println(host);
            System.out.println(statusCode);
            System.out.println(headers);
            String responseBody = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();

            List<T> list = mapper.readValue(responseBody,new TypeReference<List<T>>(){});

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ESIndexObject> getESIndexByName(String index) {
        if(index==null) index = "";
        String strQuery = "/_cat/indices/*"+index+"*?v&h=uuid,health,status,index,docsCount,storeSize,cds&s=cds:desc&format=json";
        List<ESIndexObject> list =  getDataByQuery("GET",strQuery);
        return list;
    }


    /**
     * es获取索引api不支持分页，需要实现排序和分页，未完成。
     * 从实际业务出发，索引应该不会太多，采用client分页也可以接受。
     * @param pageable
     * @return
     */
    @Override
    public Page<ESIndexObject> getAllESIndex(Pageable pageable) {
        String strQuery = "/_cat/indices?v&h=uuid,health,status,index,docsCount,storeSize,cds&s=cds:desc&format=json";

        List<ESIndexObject> list = getDataByQuery("GET",strQuery);
        int totalElements = list.size();
        if(pageable==null)
        {
            pageable = PageRequest.of(0,10);
        }

        int fromIndex = pageable.getPageSize()*pageable.getPageNumber();
        int toIndex = pageable.getPageSize()*(pageable.getPageNumber()+1);
        if(toIndex>totalElements) toIndex = totalElements;
        List<ESIndexObject> indexObjects = list.subList(fromIndex,toIndex);

        Page<ESIndexObject> page = new PageImpl<>(indexObjects,pageable,totalElements);
        return page;
    }

    @Override
    public Page<ESIndexObject> getESIndexByName(String index, Pageable pageable) {
        return null;
    }
}
