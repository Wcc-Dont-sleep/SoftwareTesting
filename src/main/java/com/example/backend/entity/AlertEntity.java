package com.example.backend.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class AlertEntity {
    private String id;
    @JSONField(serialize=false)
    private String pod;
    private Long time;
    private  String entity_id;
    private  String category;
    private  String description;
    private  String status;
    private  String entity_name;
}
