// LoginRequest.java
package com.joe.dailymate.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}