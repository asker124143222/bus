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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ESIndexServiceImpl implements ESIndexService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    RestClient restClient;

    @Override
    public Page<ESIndexObject> getAllESIndex(Pageable pageable) {
        String strQuery = "/_cat/indices?v&h=uuid,health,status,index,docsCount,storeSize,cds&s=cds:desc&format=json";
        Request request = new Request("GET", strQuery);
        try {
            Response response = restClient.performRequest(request);
//            RequestLine requestLine = response.getRequestLine();
//            HttpHost host = response.getHost();
//            int statusCode = response.getStatusLine().getStatusCode();
//            Header[] headers = response.getHeaders();
            String responseBody = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            List<ESIndexObject> list = mapper.readValue(responseBody,new TypeReference<List<ESIndexObject>>(){});
            System.out.println(response);
//            System.out.println(responseBody);
            if(pageable==null)
            {
                pageable = PageRequest.of(0,10);
            }
            Page<ESIndexObject> page = new PageImpl<>(list,pageable,list.size());
            return page;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<ESIndexObject> getESIndexByName(String indexName, Pageable pageable) {
        return null;
    }
}
