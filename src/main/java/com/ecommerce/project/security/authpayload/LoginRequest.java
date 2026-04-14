package com.ecommerce.project.security.authpayload;


import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
