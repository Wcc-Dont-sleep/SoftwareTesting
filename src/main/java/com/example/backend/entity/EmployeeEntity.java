package com.example.backend.entity;

import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;

@Data
public class EmployeeEntity {
    private String id;
    private String name;
    private String gender;
    private String occupation;
    private String birthday;
    private String avatar;
    private String cover;
}