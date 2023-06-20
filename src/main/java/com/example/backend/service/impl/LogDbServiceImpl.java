package com.example.backend.service.impl;

import com.example.backend.entity.MyLog;
import com.example.backend.entity.NewLog;
import com.example.backend.service.LogDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogDbServiceImpl implements LogDbService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public List<NewLog> getLog(String status, String dataset) {
        Query query = new Query();
        String TableName = status+"_" + dataset;

        List<NewLog> resultList = mongoTemplate.find(query, NewLog.class, TableName);
        for (NewLog result:resultList
        ) {
            result.setStatus(status);
        }

        return resultList;
    }
}
