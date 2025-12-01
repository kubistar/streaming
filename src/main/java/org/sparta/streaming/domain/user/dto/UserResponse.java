// ========================================
// UserResponse.java
// ========================================
package org.sparta.streaming.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sparta.streaming.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Integer userId;
    private String email;
    private String username;
    private String role;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}