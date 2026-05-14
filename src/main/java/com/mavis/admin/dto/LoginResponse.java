package com.mavis.admin.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String nickname;
    private String role;
}
