package org.sparta.streaming.user.dto;


import lombok.Getter;
import lombok.Setter;
import org.sparta.streaming.user.entity.User;

@Getter
@Setter
public class LoginResponseDTO {
    private String email;
    private String userType;

    public LoginResponseDTO(User user) {
        this.email = user.getEmail();
        this.userType = user.getUserRole().toString();
    }
}