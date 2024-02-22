package com.example.market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Bean을 비롯해서 여러 설정을 하기 위한 Bean 객체
@Configuration
public class WebSecurityConfig {
    // 메서드의 결과를 Bean 객체로 관리해주는 어노테이션
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        // /no-auth로 오는 요청은 모두 허가
                        auth -> auth
                                // 어떤 경로에 대한 설정인지
                                .requestMatchers(
                                        "/no-auth",
                                        "/token/issue"
                                )
                                // 이 경로에 도달할 수 있는 사람에 대한 설정 (모두)
                                .permitAll()
                                .requestMatchers(
                                        "/re-auth",
                                        "/users/my-profile"
                                )
                                .authenticated()
                                .requestMatchers(
                                        "/",
                                        "/users/register"
                                )
                                .anonymous()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                );
        return http.build();
    }

    @Bean
    // 비밀번호 암호화 클래스
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
