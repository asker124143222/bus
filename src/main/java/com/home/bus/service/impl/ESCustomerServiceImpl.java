package com.home.bus.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.bus.entity.esIndex.ESCustomer;
import com.home.bus.service.ESCustomerService;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ESCustomerServiceImpl implements ESCustomerService {
    @Resource
    RestClient restClient;

    @Resource(name = "highLevelClient")
    RestHighLevelClient restHighLevelClient;

    private ObjectMapper mapper = new ObjectMapper();

    private String indexName="customer";

    private <T> List<T> wrapperData(SearchResponse response) throws Exception
    {
        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("Customer search totalHits:"+totalHits);
        List<T> list = new ArrayList<>();
        SearchHit[] searchHits = hits.getHits();
        //SearchHits实现了Iterable接口，可以直接进行迭代
        //根据测试这里可以用hits变量替换searchHits变量
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex(); //获取文档的index
            String type = hit.getType(); //获取文档的type
            String id = hit.getId(); //获取文档的id
            Map<String, Object> sourceMap = hit.getSourceAsMap(); //获取文档内容，封装为map
            System.out.println("index:"+index+",type:"+type+",id:"+id+",\nsource:"+sourceMap);
            String sourceString = hit.getSourceAsString(); //获取文档内容，转换为json字符串。
            T object = mapper.readValue(sourceString,new TypeReference<T>(){});
            list.add(object);
        }
        return list;
    }
    @Override
    public Page<ESCustomer> searchAllInPage(Pageable pageable) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(pageable.getPageNumber()*pageable.getPageSize());
        builder.size(pageable.getPageSize());
        Sort sort = pageable.getSort();
        Iterator iterator = sort.iterator();
        while (iterator.hasNext())
        {
            Sort.Order order = (Sort.Order) iterator.next();
            //用keyword字段来排序，所以在建立索引的时候，就必须同步建立keyword字段
            builder.sort(order.getProperty()+".keyword",order.getDirection()== Sort.Direction.ASC ? SortOrder.ASC:SortOrder.DESC);
        }
        builder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(builder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

            List<ESCustomer> esCustomerList = wrapperData(searchResponse);
            Page<ESCustomer> esCustomerPage = new PageImpl<ESCustomer>(esCustomerList,pageable,searchResponse.getHits().getTotalHits());
            return esCustomerPage;

        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<ESCustomer> searchAllByMultiMatch(String query, Pageable pageable) {
        return null;
    }

    @Override
    public List<ESCustomer> searchAll() {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            List<ESCustomer> esCustomerList = wrapperData(searchResponse);
            return esCustomerList;

        }catch (Exception e)
        {
            e.printStackTrace();
            return  null;
        }
    }

    // 需要自己实现返回的json数据封装，暂时未实现
    @Deprecated
    public List<ESCustomer> searchByNameByRaw(String name) {
        String query ="{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"name\": {\n" +
                "        \"query\": \""+name+"\"\n" +
                "        , \"analyzer\": \"ik_smart\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\": {\n" +
                "    \"fields\": {\"name\": {}}\n" +
                "  }\n" +
                "}";
        Request request = new Request("GET","/user*/_search");
        HttpEntity httpEntity = new NStringEntity(query, ContentType.APPLICATION_JSON);
        request.setEntity(httpEntity);

        try {
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println(response);
            System.out.println(responseBody);
            JSONObject jsonObject = JSON.parseObject(responseBody);
            Object hits = jsonObject.get("hits");
            System.out.println(hits);
            ObjectMapper mapper = new ObjectMapper();
            Map mapResponseBody = mapper.readValue(responseBody,Map.class);
            Object source = mapResponseBody.get("hits");
            System.out.println(source);
//            List<ESCustomer> list = mapper.readValue(responseBody,new TypeReference<List<ESCustomer>>(){});

//            return list;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
