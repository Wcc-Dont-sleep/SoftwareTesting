package com.example.backend.controller;

import com.alibaba.fastjson.JSON;
import com.example.backend.entity.*;
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
import java.util.*;

@RestController
public class MetricDbController {
    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping(value = "metricdb",method  = RequestMethod.GET)
    public ResponseEntity<String> getMetric(
            @RequestParam(required = false) String dataset,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "999999999") Long time_end)
            throws IOException
    {

        Query query = new Query();
        System.out.println(model);
        Map res = new HashMap<>();

        List<DataEntity> datatList = mongoTemplate.find(query, DataEntity.class, dataset);
        List<ModelEntity> modelList = mongoTemplate.find(query, ModelEntity.class,dataset+model);
        List<Map> resultList = new ArrayList<>();
        for(int i = 0,j =0;i<datatList.size()&&j<modelList.size();i++,j++)
        {
            DataEntity data = datatList.get(i);
            ModelEntity modelEntity = modelList.get(j);
            Map tmp = new HashMap();
            tmp.put("time",data.getTime());
            tmp.put("score",modelEntity.getScore());
            tmp.put("value",data.getData());
            resultList.add(tmp);
        }


        res.put("series",resultList);
        res.put("probability",0.9);

        return new ResponseEntity<String>(JSON.toJSONString(res), HttpStatus.OK);

    }
}
