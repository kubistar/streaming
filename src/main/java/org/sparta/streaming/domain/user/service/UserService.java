// ========================================
// UserService.java
// ========================================
package org.sparta.streaming.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.user.dto.SignupRequest;
import org.sparta.streaming.domain.user.dto.UserResponse;
import org.sparta.streaming.domain.user.entity.User;
import org.sparta.streaming.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.createUser(
                request.getEmail(),
                encodedPassword,
                request.getUsername()
        );

        // 저장
        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    /**
     * 이메일로 사용자 조회
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    /**
     * ID로 사용자 조회
     */
    public User findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    /**
     * 판매자로 권한 업그레이드
     */
    @Transactional
    public void upgradeToSeller(Integer userId) {
        User user = findById(userId);
        user.upgradeToSeller();
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = findById(userId);

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 변경
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);
    }

    /**
     * 사용자명 변경
     */
    @Transactional
    public void changeUsername(Integer userId, String newUsername) {
        User user = findById(userId);
        user.changeUsername(newUsername);
    }
}
