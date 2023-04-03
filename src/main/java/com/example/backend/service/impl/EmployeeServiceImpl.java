package com.example.backend.service.impl;

import com.example.backend.dto.AllEmployeeInfoOutDto;
import com.example.backend.dto.OneEmployeeInDto;
import com.example.backend.dto.OneEmployeeOutDto;
import com.example.backend.entity.AlertEntity;
import com.example.backend.entity.EmployeeEntity;
import com.example.backend.service.EmployeeService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public List<AllEmployeeInfoOutDto> getAllEmployeeInfo() {
        List<EmployeeEntity> employeeEntities = mongoTemplate.findAll(EmployeeEntity.class, "User");
        List<AllEmployeeInfoOutDto> result = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        try {
            for (EmployeeEntity employeeEntity : employeeEntities) {
                AllEmployeeInfoOutDto allEmployeeInfoDto = modelMapper.map(employeeEntity, AllEmployeeInfoOutDto.class);
                Integer hash = (employeeEntity.getId() + "hash_id").hashCode();
                allEmployeeInfoDto.setAttendance_rate(Double.valueOf(hash % 100));
                allEmployeeInfoDto.setAward_times(Double.valueOf(hash % 10));
                result.add(allEmployeeInfoDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public boolean addEmployee(OneEmployeeInDto employeeDto) {
        List<EmployeeEntity> employees = mongoTemplate.findAll(EmployeeEntity.class, "User");
        BigInteger id = BigInteger.ZERO;
        for (EmployeeEntity e : employees) {
            BigInteger eid=BigInteger.valueOf(Long.parseLong(e.getId().substring(4)));
            if (eid.compareTo(id) > 0) id = eid;
        }
        id = id.add(BigInteger.ONE);
        employeeDto.setId("user"+id.toString());

        ModelMapper modelMapper = new ModelMapper();
        EmployeeEntity employee = modelMapper.map(employeeDto, EmployeeEntity.class);

        try {
            mongoTemplate.insert(employee, "User");
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        return true;
    }

    @Override
    public boolean updateEmployee(OneEmployeeInDto employeeDto) {
        try {
            EmployeeEntity employee = mongoTemplate.findById(employeeDto.getId(), EmployeeEntity.class, "User");
            employee.setAvatar(employeeDto.getAvatar());
            employee.setName(employeeDto.getName());
            employee.setBirthday(employeeDto.getBirthday());
            employee.setGender(employeeDto.getGender());
            employee.setCover(employeeDto.getCover());
            employee.setOccupation(employeeDto.getOccupation());

            Query query = new Query(Criteria.where("_id").is(employeeDto.getId()));
            Update update = new Update();

            update.set("name", employee.getName());
            update.set("avatar", employee.getAvatar());
            update.set("birthday", employee.getBirthday());
            update.set("gender", employee.getGender());
            update.set("cover", employee.getCover());
            update.set("occupation", employee.getOccupation());

            UpdateResult upsert = mongoTemplate.upsert(query, update, EmployeeEntity.class, "User");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteEmployee(String id) {
        try {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, EmployeeEntity.class, "User");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public OneEmployeeOutDto getOneEmployeeInfo(String id) {
        try {
            EmployeeEntity employee = mongoTemplate.findById(id, EmployeeEntity.class, "User");
            if (employee == null) return null;

            OneEmployeeOutDto out = new OneEmployeeOutDto();
            out.setAttends(null);
            out.setPrizes(null);
            out.setPayrolls(null);
            out.setId(id);
            out.setGender(employee.getGender());
            out.setAvatar(employee.getAvatar());
            out.setBirthday(employee.getBirthday());
            out.setName(employee.getName());
            out.setCover(employee.getCover());
            out.setOccupation(employee.getOccupation());
            return out;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
