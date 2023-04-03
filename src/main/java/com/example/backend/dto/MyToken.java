package com.example.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class MyToken {
    private boolean active;
    private String client_id;
    private String username;
    private String token_type;
    private int exp;
    private int iat;
    private int nbf;
    private String sub;
    private String iss;
    private List<String> aud;
}