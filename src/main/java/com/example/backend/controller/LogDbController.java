package com.example.backend.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.backend.dto.AlertStatusDto;
import com.example.backend.entity.*;
import com.example.backend.entity.HDFSEntity;
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
import java.util.*;

@RestController
public class LogDbController {
    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping(value = "log/normal",method  = RequestMethod.GET)
    public ResponseEntity<String> getNormalLog(
            @RequestParam(required = false) String dataset,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "2147483647000") Long time_end)
            throws IOException
    {

        Query query = new Query();
        MyLog log = new MyLog();
        System.out.println(model);
        Map res = new HashMap<>();
        String TableName = "normal_" + dataset;

        List<NewLog> resultList = mongoTemplate.find(query, NewLog.class, TableName);
        res.put("logging",resultList);
        res.put("probability",0.9);
        res.put("threshold",null);

//        System.out.println(resultList);

        return new ResponseEntity<String>(JSON.toJSONString(res), HttpStatus.OK);

    }
    @RequestMapping(value = "log/abnormal",method  = RequestMethod.GET)
    public ResponseEntity<String> getAbnormalLog(
            @RequestParam(required = false) String dataset,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "2147483647000") Long time_end)
            throws IOException
    {

        Query query = new Query();
        MyLog log = new MyLog();
        System.out.println(model);
        Map res = new HashMap<>();

        String TableName = "abnormal_" + dataset;

        List<NewLog> resultList = mongoTemplate.find(query, NewLog.class, TableName);
        res.put("logging",resultList);
        res.put("probability",0.9);
        res.put("threshold",null);


        return new ResponseEntity<String>(JSON.toJSONString(res), HttpStatus.OK);

    }
}
