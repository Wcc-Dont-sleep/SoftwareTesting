package com.example.backend.entity;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class NewMetric {
    private String f1;
    public String datetime;
    public String value;
    private String label;
    public String score;
}