package org.sparta.streaming.user.service;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.user.entity.User;
import org.sparta.streaming.user.repository.UserRepository;
import org.sparta.streaming.user.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User signup(String useremail, String password) {
        User newMember = new User(useremail, passwordEncoder.encode(password));
        return userRepository.save(newMember);
    }

    public Map<String, String> login(String useremail, String password) {
        User user = userRepository.findByUseremail(useremail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String accessToken = jwtUtil.createToken(user.getUserId(), useremail, user.getRole().name());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        return tokens;
    }


    public void updateUserToUploader(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));
        user.updateToUploader();
        userRepository.save(user);
    }


}
