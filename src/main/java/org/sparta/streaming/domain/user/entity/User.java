// ========================================
// User.java
// ========================================
package org.sparta.streaming.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========================================
    // Builder 패턴
    // ========================================
    @Builder
    private User(String email, String password, String username, Role role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role != null ? role : Role.USER;
    }

    // ========================================
    // 정적 팩토리 메서드
    // ========================================

    /**
     * 일반 사용자 생성
     */
    public static User createUser(String email, String encodedPassword, String username) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .username(username)
                .role(Role.USER)
                .build();
    }

    /**
     * 판매자(크리에이터) 생성
     */
    public static User createSeller(String email, String encodedPassword, String username) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .username(username)
                .role(Role.SELLER)
                .build();
    }

    // ========================================
    // 비즈니스 로직
    // ========================================

    /**
     * 일반 사용자 → 판매자로 권한 업그레이드
     */
    public void upgradeToSeller() {
        if (this.role == Role.SELLER) {
            throw new IllegalStateException("이미 판매자 권한입니다.");
        }
        this.role = Role.SELLER;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    /**
     * 사용자명 변경
     */
    public void changeUsername(String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 비어있을 수 없습니다.");
        }
        this.username = newUsername;
    }

    /**
     * 판매자 권한 확인
     */
    public boolean isSeller() {
        return this.role == Role.SELLER;
    }
}