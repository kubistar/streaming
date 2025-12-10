package org.sparta.streaming.domain.user.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.streaming.domain.user.jwt.JwtAuthenticationFilter;
import org.sparta.streaming.domain.user.jwt.JwtAuthorizationFilter;
import org.sparta.streaming.domain.user.security.UserDetailsServiceImpl;
import org.sparta.streaming.domain.user.util.JwtUtil;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 권한 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        // 정적 리소스 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // 메인 페이지 허용
                        .requestMatchers("/").permitAll()

                        // 회원가입, 로그인 허용
                        .requestMatchers("/api/users/signup").permitAll()
                        .requestMatchers("/api/users/login").permitAll()

                        // 🔥 더미 데이터 및 배치 API 허용 (개발/테스트용)
                        .requestMatchers("/api/dummy/**").permitAll()
                        .requestMatchers("/api/batch/**").permitAll()

                        // 동영상 조회 허용
                        .requestMatchers("/api/videos/**").permitAll()

                        // 스트리밍
                        .requestMatchers("/api/streaming/**").authenticated()

                        // 🔥 통계 API - 판매자만 접근 가능
                        .requestMatchers("/api/statistics/**").hasRole("SELLER")

                        // 🔥 정산 API - 판매자만 접근 가능
                        .requestMatchers("/api/settlement/**").hasRole("SELLER")

                        // 판매자 권한 필요
                        .requestMatchers("/api/seller/**").hasRole("SELLER")

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
        );

        // Exception Handling 설정
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.authenticationEntryPoint(new FailedAuthenticationEntryPoint())
        );

        // 필터 순서 중요!
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 인증 실패 시 처리
     */
    class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"error\":\"인증 실패\",\"message\":\"로그인이 필요한 서비스입니다.\"}"
            );
            log.error("Unauthorized error: {}", authException.getMessage());
        }
    }
}