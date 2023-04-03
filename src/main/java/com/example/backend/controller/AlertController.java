package com.example.backend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.backend.dto.AlertStatusDto;
import com.example.backend.entity.AlertEntity;
import org.apache.catalina.connector.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class AlertController {

    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping(value = "alarms", method = RequestMethod.GET)
    public ResponseEntity<String> getAlerts(
            @RequestParam(required = false, defaultValue = "0") Long start_time,
            @RequestParam(required = false, defaultValue = "9999999999") Long end_time)
            throws IOException
    {
        Query query = Query.query(Criteria.where("time")
                .lte(end_time)
                .gte(start_time)
        );

        List<AlertEntity> resultList = mongoTemplate.find(query, AlertEntity.class, "Test");
//        System.out.println(resultList);
        return new ResponseEntity<String>(JSON.toJSONString(resultList), HttpStatus.OK);
    }

    @RequestMapping(value = "alarm", method = RequestMethod.POST)
    public ResponseEntity postAlerts(
            @RequestBody AlertStatusDto body
    )
    {
        Query query = Query.query(Criteria.where("id")
                .is(body.getId())
        );

        List<AlertEntity> list = mongoTemplate.find(query, AlertEntity.class, "Test");
        if (list.size() == 0) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            Update update = Update.update("status", body.getStatus());
            mongoTemplate.updateFirst(query, update, AlertEntity.class, "Test");
            return new ResponseEntity(HttpStatus.OK);
        }
    }
}
