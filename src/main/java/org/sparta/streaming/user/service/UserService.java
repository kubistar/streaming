package org.sparta.streaming.user.service;


import java.util.Optional;

import org.sparta.streaming.user.dto.LoginResponseDTO;
import org.sparta.streaming.user.dto.UserRequestDTO;
import org.sparta.streaming.user.dto.UserResponseDTO;
import org.sparta.streaming.user.entity.User;
import org.sparta.streaming.user.entity.UserRoleEnum;
import org.sparta.streaming.user.exception.UserException;
import org.sparta.streaming.user.jwt.JwtUtil;
import org.sparta.streaming.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 1. 회원 가입
     * @param requestDTO 회원 가입 요청 데이터
     * @return UserResponseDTO 회원 가입 결과
     */
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        //이메일 유효성 검사
        validateUserEmail(requestDTO.getEmail());

        //비밀번호 암호화
        String password = passwordEncoder.encode(requestDTO.getPassword());

        User user = User.builder()
                .password(password)
                .email(requestDTO.getEmail())
                .userRole(UserRoleEnum.USER)
                .build();

        User saveUser = userRepository.save(user);

        return new UserResponseDTO(saveUser);
    }

    @Transactional
    public LoginResponseDTO login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String refreshToken = jwtUtil.createRefreshToken(email);
        user.refreshTokenReset(refreshToken);
        User saveUser = userRepository.save(user);

        return new LoginResponseDTO(saveUser);
    }

    /**
     * 3. 로그아웃
     * @param user 로그인한 사용자의 세부 정보
     * @param accessToken access token
     */
    @Transactional
    public void logout(User user, String accessToken) {

        if(user == null){
            throw new UserException("로그인되어 있는 유저가 아닙니다.");
        }

        // checkUserType(user.getUserType());

        User existingUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UserException("해당 유저가 존재하지 않습니다."));

        String refreshToken = existingUser.getRefreshToken();
        existingUser.refreshTokenReset("");
        userRepository.save(existingUser);

        jwtUtil.invalidateToken(accessToken);
        jwtUtil.invalidateToken(refreshToken);
    }


    /**
     * 이메일 유효성 검사
     * @param email 이메일
     */
    private void validateUserEmail(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        if(findUser.isPresent()) {
            throw new UserException("중복된 Email 입니다.");
        }
    }


}