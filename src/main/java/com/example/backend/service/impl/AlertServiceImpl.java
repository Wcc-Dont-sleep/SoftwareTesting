package com.example.backend.service.impl;

import com.example.backend.entity.AlertEntity;
import com.example.backend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlertServiceImpl implements AlertService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public List<AlertEntity> getAlert(Long start_time, Long end_time) {
        Query query = Query.query(Criteria.where("time")
                .lte(end_time)
                .gte(start_time)
        );

        List<AlertEntity> resultList = mongoTemplate.find(query, AlertEntity.class, "Test");
        return resultList;
    }
}
