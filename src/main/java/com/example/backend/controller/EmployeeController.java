package com.example.backend.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.backend.dto.*;
import com.example.backend.service.EmployeeService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@RestController
public class EmployeeController {
    private String baseUrl = "http://39.98.47.69:8000/";
    private String authorization = "Basic ODAwZTA2ODg5NGFiZjVkMmQ0MTE6Y2ZmODhmYWJlMGJiMDY3Y2I1NmM2OGQ4ZmUzZjk0MWY0ODJmMmU5Yg==";
    private HttpClient client = new HttpClient();
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public ResponseEntity<TokenInfo> checkToken(String token) throws IOException {
        client.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        PostMethod postMethod = new PostMethod(baseUrl + "api/login/oauth/introspect");

        postMethod.addRequestHeader("Content-type",
                "application/x-www-form-urlencoded");
        postMethod.addRequestHeader("accept",
                "application/json");
        postMethod.addRequestHeader("Authorization",
                authorization);

        postMethod.addParameter("token", token);
        postMethod.addParameter("token_type_hint", "access_token");


        client.executeMethod(postMethod);
        byte[] responseBody = postMethod.getResponseBody();
        String s = new String(responseBody);

        JSONObject response = JSONObject.parseObject(s);

        ModelMapper modelMapper = new ModelMapper();
        MyToken myToken = modelMapper.map(response, MyToken.class);

        TokenInfo info = new TokenInfo();
        info.setActive(myToken.isActive());
        info.setUsername(myToken.getUsername());

        System.out.println("look this is username:"+info.getUsername()+" this is activate:"+info.getActive());

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    // Get all employee info
    @RequestMapping(value = "employees", method = RequestMethod.GET)
    public ResponseEntity<List<AllEmployeeInfoOutDto>> getAllEmployees(
            @RequestParam(value = "token", required = false) String token
    ) throws IOException {
//        // check token
//        if (token == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        ResponseEntity<TokenInfo> check = checkToken(token);
//        if (check.getBody() == null || !check.getBody().getActive())
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<AllEmployeeInfoOutDto> result = employeeService.getAllEmployeeInfo();
        if (result.size() == 0) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return new ResponseEntity<List<AllEmployeeInfoOutDto>>(result, HttpStatus.OK);
    }

    // Get one employee info
    @RequestMapping(value = "employee", method = RequestMethod.GET)
    public ResponseEntity<OneEmployeeOutDto> getAllEmployees(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "token", required = false) String token
    ) throws IOException {

        System.out.println("id:"+id+"  "+"token:"+token);
        // check token
        if (token == null) //return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        {
            token = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImNlcnQtYnVpbHQtaW4iLCJ0eXAiOiJKV1QifQ.eyJvd25lciI6Im9yZ2FuaXphdGlvbl9rbm93bGVkZ2UiLCJuYW1lIjoiYWRtaW4iLCJjcmVhdGVkVGltZSI6IjIwMjMtMDQtMjBUMDI6MTg6MjVaIiwidXBkYXRlZFRpbWUiOiIiLCJpZCI6ImNjZWJmYTVjLWE4MDMtNGE1YS1iY2I2LTIxNDczMzA3Mzc0ZSIsInR5cGUiOiJub3JtYWwtdXNlciIsInBhc3N3b3JkIjoiIiwicGFzc3dvcmRTYWx0IjoiIiwiZGlzcGxheU5hbWUiOiIiLCJmaXJzdE5hbWUiOiIiLCJsYXN0TmFtZSI6IiIsImF2YXRhciI6Imh0dHBzOi8vY2RuLmNhc2Jpbi5vcmcvaW1nL2Nhc2Jpbi5zdmciLCJwZXJtYW5lbnRBdmF0YXIiOiIiLCJlbWFpbCI6IjEyMzQ1NkBxcS5jb20iLCJlbWFpbFZlcmlmaWVkIjpmYWxzZSwicGhvbmUiOiIiLCJsb2NhdGlvbiI6IiIsImFkZHJlc3MiOltdLCJhZmZpbGlhdGlvbiI6IiIsInRpdGxlIjoiIiwiaWRDYXJkVHlwZSI6IiIsImlkQ2FyZCI6IiIsImhvbWVwYWdlIjoiIiwiYmlvIjoiIiwicmVnaW9uIjoiIiwibGFuZ3VhZ2UiOiIiLCJnZW5kZXIiOiIiLCJiaXJ0aGRheSI6IiIsImVkdWNhdGlvbiI6IiIsInNjb3JlIjoyMDAwLCJrYXJtYSI6MCwicmFua2luZyI6MTIsImlzRGVmYXVsdEF2YXRhciI6ZmFsc2UsImlzT25saW5lIjpmYWxzZSwiaXNBZG1pbiI6ZmFsc2UsImlzR2xvYmFsQWRtaW4iOmZhbHNlLCJpc0ZvcmJpZGRlbiI6ZmFsc2UsImlzRGVsZXRlZCI6ZmFsc2UsInNpZ251cEFwcGxpY2F0aW9uIjoiYXBwbGljYXRpb25fa25vd2xlZGdlIiwiaGFzaCI6IiIsInByZUhhc2giOiIiLCJjcmVhdGVkSXAiOiIiLCJsYXN0U2lnbmluVGltZSI6IiIsImxhc3RTaWduaW5JcCI6IiIsImdpdGh1YiI6IiIsImdvb2dsZSI6IiIsInFxIjoiIiwid2VjaGF0IjoiIiwiZmFjZWJvb2siOiIiLCJkaW5ndGFsayI6IiIsIndlaWJvIjoiIiwiZ2l0ZWUiOiIiLCJsaW5rZWRpbiI6IiIsIndlY29tIjoiIiwibGFyayI6IiIsImdpdGxhYiI6IiIsImFkZnMiOiIiLCJiYWlkdSI6IiIsImFsaXBheSI6IiIsImNhc2Rvb3IiOiIiLCJpbmZvZmxvdyI6IiIsImFwcGxlIjoiIiwiYXp1cmVhZCI6IiIsInNsYWNrIjoiIiwic3RlYW0iOiIiLCJiaWxpYmlsaSI6IiIsIm9rdGEiOiIiLCJkb3V5aW4iOiIiLCJjdXN0b20iOiIiLCJ3ZWJhdXRobkNyZWRlbnRpYWxzIjpudWxsLCJsZGFwIjoiIiwicHJvcGVydGllcyI6e30sInJvbGVzIjpbXSwicGVybWlzc2lvbnMiOltdLCJsYXN0U2lnbmluV3JvbmdUaW1lIjoiIiwic2lnbmluV3JvbmdUaW1lcyI6MCwibWFuYWdlZEFjY291bnRzIjpudWxsLCJ0b2tlblR5cGUiOiJhY2Nlc3MtdG9rZW4iLCJzY29wZSI6InJlYWQiLCJpc3MiOiJodHRwczovLzM5Ljk4LjQ3LjY5OjgwMDAiLCJzdWIiOiJjY2ViZmE1Yy1hODAzLTRhNWEtYmNiNi0yMTQ3MzMwNzM3NGUiLCJhdWQiOlsiODAwZTA2ODg5NGFiZjVkMmQ0MTEiXSwiZXhwIjoxNjgyNTYxOTE1LCJuYmYiOjE2ODE5NTcxMTUsImlhdCI6MTY4MTk1NzExNSwianRpIjoiYWRtaW4vZDgwOTFmN";

        }


        ResponseEntity<TokenInfo> check = checkToken(token);
        if (check.getBody() == null || !check.getBody().getActive())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (id == null) id = check.getBody().getUsername();
        OneEmployeeOutDto result = employeeService.getOneEmployeeInfo(id);

        System.out.println(result);
        if (result == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // add a new employee
    @RequestMapping(value = "employees", method = RequestMethod.POST)
    public ResponseEntity addEmployee(@RequestBody OneEmployeeInDto employeeDto)
    {
        if(!employeeDto.getId().equals("")){
            return updateEmployee(employeeDto);
        }

        if (employeeService.addEmployee(employeeDto)) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // update a new employee
    @RequestMapping(value = "employees", method = RequestMethod.PUT)
    public ResponseEntity updateEmployee(@RequestBody OneEmployeeInDto employeeDto)
    {
        if (employeeService.updateEmployee(employeeDto)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // delete a new employee
    @RequestMapping(value = "employees", method = RequestMethod.DELETE)
    public ResponseEntity deleteEmployee(
            @RequestParam(required = true) String id,
            @RequestParam(required = false) String token
    ) throws IOException {
        // check token
        if (token == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        ResponseEntity<TokenInfo> check = checkToken(token);
        if (check.getBody() == null || !check.getBody().getActive())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (employeeService.deleteEmployee(id)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
