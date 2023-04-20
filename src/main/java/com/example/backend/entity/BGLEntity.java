package com.example.backend.entity;

import lombok.Data;

@Data
public class BGLEntity {
    private String f1;
    private String LineId;
    private String Date;
    private String Time;
    private String Level;
    private String Component;
    private String Content;
    private String isError;
}
