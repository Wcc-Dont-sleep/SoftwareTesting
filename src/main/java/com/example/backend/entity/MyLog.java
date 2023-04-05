package com.example.backend.entity;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class MyLog {
    public List<HDFSEntity> logging;
    private String probability;
    private String threshold;

}
