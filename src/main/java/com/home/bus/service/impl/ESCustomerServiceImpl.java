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
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ESCustomerServiceImpl implements ESCustomerService {
    @Resource
    RestClient restClient;
    @Override
    public List<ESCustomer> searchByName(String name) {
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
            List<ESCustomer> list = mapper.readValue(responseBody,new TypeReference<List<ESCustomer>>(){});

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
