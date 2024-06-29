package org.sparta.streaming.user.config;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.user.jwt.JwtAuthenticationFilter;
import org.sparta.streaming.user.jwt.JwtAuthorizationFilter;
import org.sparta.streaming.user.jwt.JwtUtil;
import org.sparta.streaming.user.repository.UserRepository;
import org.sparta.streaming.user.service.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;


    // 비밀번호 인코더를 정의하는 빈. BCrypt 알고리즘을 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 인증 매니저를 설정합니다. 이는 다양한 인증 관련 작업을 관리
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    // JWT 인증 필터를 설정합니다. 이 필터는 인증 토큰을 처리하여 사용자 인증을 수행
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    // JWT 권한 부여 필터를 설정합니다. 이 필터는 권한을 검증하고 요청을 처리
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, userRepository);
    }

    // HTTP 보안 구성을 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호를 비활성화
        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(requests ->
                requests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()      // 로그인 경로에 대한 접근 허용
                        .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()     // 회원가입 경로에 대한 접근 허용
                        .anyRequest().authenticated()          // 나머지 요청은 인증 필요
        );

        // 인증 필터를 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        return http.build();
    }
}