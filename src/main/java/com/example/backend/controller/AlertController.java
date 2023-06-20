package com.example.backend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.backend.dto.AlertStatusDto;
import com.example.backend.entity.AlertEntity;
import com.example.backend.service.AlertService;
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
    AlertService alertService;
    @Autowired
    public AlertController(AlertService alertService){this.alertService = alertService;}

    @RequestMapping(value = "alarms", method = RequestMethod.GET)
    public ResponseEntity<String> getAlerts(
            @RequestParam(required = false, defaultValue = "0") Long start_time,
            @RequestParam(required = false, defaultValue = "9999999999") Long end_time)
            throws IOException
    {
       List<AlertEntity> resultList = alertService.getAlert(start_time,end_time);
//        System.out.println(resultList);
        return new ResponseEntity<String>(JSON.toJSONString(resultList), HttpStatus.OK);
    }

}
