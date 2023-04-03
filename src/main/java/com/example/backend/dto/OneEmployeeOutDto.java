package com.example.backend.dto;

import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

@Data
public class OneEmployeeOutDto {
    private String id;
    private String name;
    private String gender;
    private String occupation;
    private String birthday;
    private String avatar;
    private String cover;
    private Collection<AttendInfo> attends;
    private Collection<PrizeInfo> prizes;
    private Collection<PayrollInfo> payrolls;
}