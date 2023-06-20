package com.example.backend.controller;

import com.alibaba.fastjson.JSON;
import com.example.backend.entity.*;
import com.example.backend.service.MetricDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class MetricDbController {
    MetricDbService metricDbService;
    @Autowired
    public MetricDbController(MetricDbService metricDbService){this.metricDbService = metricDbService;}
    @RequestMapping(value = "metric",method  = RequestMethod.GET)
    public ResponseEntity<String> getMetric(
            @RequestParam(required = false) String dataset,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "999999999") Long time_end)
            throws IOException
    {

        Map res = new HashMap<>();
        List<Map> resultList = metricDbService.getMetric(status,dataset);
        List<String> proList = metricDbService.getPro();
        res.put("series",resultList);
        res.put("probability",proList);

        return new ResponseEntity<String>(JSON.toJSONString(res), HttpStatus.OK);

    }
    /*
    @RequestMapping(value = "metric/abnormal",method  = RequestMethod.GET)
    public ResponseEntity<String> getAbnormalMetric(
            @RequestParam(required = false) String dataset,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "999999999") Long time_end)
            throws IOException
    {

        Query query = new Query();
        System.out.println(model);
        Map res = new HashMap<>();

        String TableName = "abnormal_" + dataset + "_mrt";
        List<NewMetric> datatList = mongoTemplate.find(query, NewMetric.class, TableName);
        List<Map> resultList = new ArrayList<>();
        for(int i = 0,j =0;i<datatList.size()&&j<datatList.size();i++,j++)
        {
            NewMetric data = datatList.get(i);
            Map tmp = new HashMap();
            tmp.put("time",data.datetime);
            tmp.put("score",data.score);
            tmp.put("value",data.value);
            resultList.add(tmp);
        }

        List<String> proList = mongoTemplate.find(query, String.class, "pro");
        res.put("series",resultList);
        res.put("probability",proList);


        return new ResponseEntity<String>(JSON.toJSONString(res), HttpStatus.OK);

    }
    */
}
