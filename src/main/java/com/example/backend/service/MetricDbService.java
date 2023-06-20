package com.example.backend.service;

import com.example.backend.entity.NewMetric;

import java.util.List;
import java.util.Map;

public interface MetricDbService {
    public List<Map> getMetric(String status, String dataset);
    public List<String> getPro();
}
