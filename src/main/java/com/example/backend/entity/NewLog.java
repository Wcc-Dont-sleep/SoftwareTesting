package com.example.backend.entity;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class NewLog {
    private String f1;
    private String log_id;
    private String timestamp;
    private String Component;
    private String log_name;
    private String Content;
    private String isError;

    private String status;
}