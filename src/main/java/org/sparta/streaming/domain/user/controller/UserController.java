package org.sparta.streaming.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.user.dto.SignupRequest;
import org.sparta.streaming.domain.user.dto.UserResponse;
import org.sparta.streaming.domain.user.security.UserDetailsImpl;
import org.sparta.streaming.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody SignupRequest request) {
        UserResponse response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그아웃
     * JWT 방식에서는 클라이언트가 토큰을 삭제하면 됨
     * 서버에서는 블랙리스트 관리가 필요하면 Redis 사용
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT는 Stateless이므로 클라이언트에서 토큰 삭제
        // 필요시 Redis에 블랙리스트 추가
        return ResponseEntity.ok("{\"message\":\"로그아웃 되었습니다.\"}");
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponse response = UserResponse.from(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    /**
     * 판매자로 업그레이드
     */
    @PostMapping("/upgrade-to-seller")
    public ResponseEntity<String> upgradeToSeller(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.upgradeToSeller(userDetails.getUser().getUserId());
        return ResponseEntity.ok("{\"message\":\"판매자 권한으로 업그레이드되었습니다.\"}");
    }
}