package com.example.backend.service;

import com.example.backend.entity.AlertEntity;

import java.util.List;

public interface AlertService {
    public List<AlertEntity> getAlert(Long start_time,Long end_time);
}
