package com.example.backend.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.backend.dto.AlertStatusDto;
import com.example.backend.entity.AlertEntity;
import com.example.backend.entity.BGLEntity;
import com.example.backend.entity.HDFSEntity;
import com.example.backend.entity.MyLog;
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

    @RequestMapping(value = "logdb",method  = RequestMethod.GET)
    public ResponseEntity<String> getLog(
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

        if(Objects.equals(dataset, "HDFS"))

        {
            List<HDFSEntity> resultList = mongoTemplate.find(query, HDFSEntity.class, dataset);
            res.put("logging",resultList);
            res.put("probability",0.9);
            res.put("threshold",null);
        }
        else
        {
            List<BGLEntity> resultList = mongoTemplate.find(query, BGLEntity.class, dataset);
            res.put("logging",resultList);
            res.put("probability",0.9);
            res.put("threshold",null);
        }
        List<HDFSEntity> resultList = mongoTemplate.find(query, HDFSEntity.class, "HDFS");
//        System.out.println(resultList);

        return new ResponseEntity<String>(JSON.toJSONString(res), HttpStatus.OK);

    }

}
