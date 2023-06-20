package com.example.backend.service.impl;

import com.example.backend.entity.NewMetric;
import com.example.backend.service.MetricDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetricDbServiceImpl implements MetricDbService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public List<Map> getMetric(String status, String dataset) {
        Query query = new Query();
        String TableName = status+"_" + dataset + "_mrt";
        List<NewMetric> datatList = mongoTemplate.find(query, NewMetric.class, TableName);
        List<Map> resultList = new ArrayList<>();
        for(int i = 0,j =0;i<datatList.size()&&j<datatList.size();i++,j++)
        {
            NewMetric data = datatList.get(i);
            Map tmp = new HashMap();
            Long datestamp = Long.parseLong(data.datetime);
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(datestamp, 0,
                    ZoneOffset.UTC); // 将时间戳转换为LocalDateTime对象
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 定义日期时间格式
            String formattedDateTime = dateTime.format(formatter); // 将LocalDateTime对象格式化为字符串
            tmp.put("time",formattedDateTime);
            tmp.put("score",data.score);
            tmp.put("value",data.value);
            resultList.add(tmp);
        }
        return resultList;
    }

    @Override
    public List<String> getPro() {
        Query query = new Query();
        List<String> proList = mongoTemplate.find(query, String.class, "pro");
        return proList;
    }
}
