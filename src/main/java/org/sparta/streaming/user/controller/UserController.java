package org.sparta.streaming.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sparta.streaming.user.dto.LoginRequest;
import org.sparta.streaming.user.dto.SignupRequest;
import org.sparta.streaming.user.entity.User;
import org.sparta.streaming.user.service.UserDetailsImpl;
import org.sparta.streaming.user.service.UserService;
import org.sparta.streaming.user.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignupRequest request) {
        User createdMember = service.signup(request.getUseremail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Map<String, String> tokens = service.login(request.getUseremail(), request.getPassword());

        // 액세스 토큰 쿠키 설정
        Cookie accessTokenCookie = new Cookie("accessToken", Base64.getUrlEncoder().encodeToString(tokens.get("accessToken").getBytes()));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS에서만 전송되도록 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간 유효

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);

        // 응답 헤더에 액세스 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, tokens.get("accessToken"));

        return ResponseEntity.ok().headers(headers).body(tokens);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(user);
    }

    @Secured("ROLE_APLOADER")
    @GetMapping("/uploader")
    public ResponseEntity<User> getAdminInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(user);
    }
}
