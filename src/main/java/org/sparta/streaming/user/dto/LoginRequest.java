package org.sparta.streaming.user.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String useremail;
    private String password;
}