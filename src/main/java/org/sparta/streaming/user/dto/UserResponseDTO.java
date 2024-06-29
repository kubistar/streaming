package org.sparta.streaming.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.sparta.streaming.user.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String email;
    private String userType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.userType = user.getUserRole().toString();
        this.createdAt = user.getCreatedAt();
        this.modifiedAt = user.getModifiedAt();
    }

}