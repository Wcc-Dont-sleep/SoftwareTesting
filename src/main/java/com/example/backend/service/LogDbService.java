package com.example.backend.service;

import com.example.backend.entity.NewLog;

import java.util.List;

public interface LogDbService {
    public List<NewLog> getLog(String status, String dataset);
}
