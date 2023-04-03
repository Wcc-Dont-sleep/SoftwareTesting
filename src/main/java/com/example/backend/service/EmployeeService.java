package com.example.backend.service;

import com.example.backend.dto.AllEmployeeInfoOutDto;
import com.example.backend.dto.OneEmployeeInDto;
import com.example.backend.dto.OneEmployeeOutDto;

import java.math.BigInteger;
import java.util.List;

public interface EmployeeService {
    public List<AllEmployeeInfoOutDto> getAllEmployeeInfo();
    public boolean addEmployee(OneEmployeeInDto employee);
    public boolean updateEmployee(OneEmployeeInDto employee);
    public boolean deleteEmployee(String id);
    public OneEmployeeOutDto getOneEmployeeInfo(String id);
}
