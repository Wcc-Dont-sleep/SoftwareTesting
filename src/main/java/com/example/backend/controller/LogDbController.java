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
        Query query = Query.query(Criteria.where("time")
                .lte(time_end)
                .gte(time_start)
        );
        List<AlertEntity> resultList = mongoTemplate.find(query, AlertEntity.class, dataset);
//        System.out.println(resultList);
        return new ResponseEntity<String>(JSON.toJSONString(resultList), HttpStatus.OK);
    }

}
