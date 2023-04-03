package com.example.backend.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.backend.entity.AlertEntity;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AlertScheduler {
    @Autowired
    MongoTemplate mongoTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String baseUrl = "http://10.60.38.174:31013/api/v1/query_range?query=ALERTS";

    @Scheduled(initialDelay=1000, fixedRate=1000 * 60 * 30)
    public void pollingAlert() throws IOException {
        long cur = System.currentTimeMillis() / 1000;

        String url = baseUrl + "&start=" + String.valueOf(cur - 60 * 30);
        url += "&end=" + String.valueOf(cur) + "&step=180s";

        logger.info(url);

        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("Content-Type", "application/json");

        httpClient.executeMethod(getMethod);

        String result = getMethod.getResponseBodyAsString();
        getMethod.releaseConnection();

        JSONObject object = JSON.parseObject(result);
        JSONObject data = JSON.parseObject(object.getString("data"));
        JSONArray data_result = JSON.parseArray(data.getString("result"));

        logger.info(data_result.toString());

        for(int i = 0; i < data_result.size(); i++) {
            JSONObject obj = data_result.getJSONObject(i);

            JSONObject metric = JSON.parseObject(obj.getString("metric"));
            JSONArray values = JSON.parseArray(obj.getString("values"));

            String pod = metric.getString("pod");
            String entity_id = metric.getString("namespace");
            String category = metric.getString("alertstate");
            String description = metric.getString("alertname");
            String status = "unread";
            String entity_name = metric.getString("container");

            for (int j = 0; j < values.size(); j++) {
                JSONArray array = values.getJSONArray(j);

                AlertEntity alertEntity = new AlertEntity();
                alertEntity.setPod(pod);
                alertEntity.setTime(Long.valueOf(array.getString(0)));
                alertEntity.setEntity_id(entity_id);
                alertEntity.setCategory(category);
                alertEntity.setDescription(description);
                alertEntity.setStatus(status);
                alertEntity.setEntity_name(entity_name);
                mongoTemplate.insert(alertEntity, "Test");
            }
        }

        Query query = new Query(Criteria.where("time").lt(String.valueOf(cur - 60 * 30)));
        mongoTemplate.remove(query, AlertEntity.class, "Test");
    }

}
