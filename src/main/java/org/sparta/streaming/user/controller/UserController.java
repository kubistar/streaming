package org.sparta.streaming.user.controller;

import org.sparta.streaming.dto.*;
import org.sparta.streaming.user.dto.*;
import org.sparta.streaming.user.jwt.JwtUtil;
import org.sparta.streaming.user.service.UserDetailsImpl;
import org.sparta.streaming.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 1. 회원 가입
     * @param requestDTO 회원 가입 요청 데이터
     * @return ResponseEntity<ResponseMessage<UserResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     * 	   - 상태 코드: 회원 가입이 성공적으로 이루어지면 201 (CREATED)
     * 	   - 메시지: 회원 가입 상태를 설명하는 메시지
     * 	   - 데이터: 생성된 회원의 정보를 담고 있는 UserResponseDTO 객체
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.createUser(requestDTO);

        ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("회원가입이 완료되었습니다.")
                .data(responseDTO)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }



    /**
     * 사용자 로그인을 처리하는 API
     *
     * @param requestDTO 로그인 요청 데이터를 포함하는 DTO 객체. 이 객체에는 사용자의 이메일과 비밀번호가 포함되어 있습니다.
     * @return ResponseEntity<ResponseMessage<LoginResponseDTO>> 로그인 성공 시 상태 코드 200과 함께,
     *         로그인에 성공한 사용자의 정보와 JWT 액세스 토큰이 포함된 응답을 반환합니다.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<LoginResponseDTO>> loginUser(@RequestBody LoginRequestDTO requestDTO) {
        // UserService를 사용하여 전달받은 이메일과 비밀번호로 로그인 시도.
        LoginResponseDTO responseDTO = userService.login(requestDTO.getEmail(), requestDTO.getPassword());

        // JwtUtil을 통해 로그인 성공한 사용자의 이메일을 기반으로 JWT 액세스 토큰 생성.
        String accessToken = jwtUtil.createAccessToken(requestDTO.getEmail());

        // HTTP 헤더를 생성하고, 'Authorization' 헤더에 'Bearer ' 접두사와 함께 생성된 액세스 토큰을 추가.
        HttpHeaders headers = new HttpHeaders();
        headers.add(jwtUtil.AUTHORIZATION_HEADER, accessToken);


        // 로그인에 성공했을 때 반환할 응답 메시지를 구성.
        // 여기에는 HTTP 상태 코드, 메시지, 그리고 로그인한 사용자의 상세 정보가 포함
        ResponseMessage<LoginResponseDTO> responseMessage = ResponseMessage.<LoginResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("로그인이 완료되었습니다.")
                .data(responseDTO)
                .build();

        // ResponseEntity 객체를 생성하여 로그인 응답을 클라이언트에 전달.
        // 응답 헤더와 HTTP 상태 코드, 그리고 로그인 응답 메시지를 포함
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseMessage);
    }



    /**
     * 3. 로그아웃
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param request HTTP 요청
     * @return ResponseEntity<ResponseMessage<String>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     * 	   - 상태 코드: 로그아웃이 성공적으로 이루어지면 200 (OK)
     * 	   - 메시지: 로그아웃 상태를 설명하는 메시지
     * 	   - 데이터: 로그아웃된 회원의 이메일
     */
    @GetMapping("/logout")
    public ResponseEntity<ResponseMessage<String>> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request){

        String accessToken = jwtUtil.getJwtFromHeader(request);
        userService.logout(userDetails.getUser(), accessToken);

        ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("로그아웃이 완료되었습니다.")
                .data(userDetails.getUsername())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

}