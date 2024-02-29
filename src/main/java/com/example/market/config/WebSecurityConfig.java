package com.example.market.config;

import com.example.market.jwt.JwtTokenFilter;
import com.example.market.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

// @Bean을 비롯해서 인증 등 여러 설정을 하기 위한 Bean 객체
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    // 메서드의 결과를 Bean 객체로 관리해주는 어노테이션
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http // 인증과 권한 관련 설정을 적용할 수 있는 객체
    ) throws Exception {
        http
                // csrf 보안 해제
                .csrf(AbstractHttpConfigurer::disable)
                // url에 따른 요청 인가
                .authorizeHttpRequests(
                        // /no-auth로 오는 요청은 모두 허가
                        auth -> auth
                                // 어떤 경로에 대한 설정인지
                                .requestMatchers(
                                        "/token/issue",
                                        "/token/validate",
                                        "/users/profile-info",
                                        "/users/avatar"
                                )
                                // 이 경로에 도달할 수 있는 사람에 대한 설정 (모두)
                                .permitAll()
                                .requestMatchers(
                                        "/users/my-profile"

                                )
                                .authenticated()

                                .requestMatchers(
                                        "/users/signup"
                                )
                                .anonymous()

                                // ROLE_USER 접근 가능
                                .requestMatchers(
                                        "/auth/user-role",
                                        "/users/register-business",
                                        "/items",
                                        "/items/register",
                                        "/items/register-image",
                                        "/items/update/{itemId}",
                                        "/items/delete/{itemId}",
                                        "/items/{itemId}/proposals",
                                        "/items/{itemId}/proposals/{proposalId}/accept",
                                        "/items/{itemId}/proposals/{proposalId}/reject",
                                        "/items/{itemId}/proposals/{proposalId}/confirm"
                                )
                                .hasAnyRole("USER", "BUSINESS_USER", "ADMIN")

                                // ROLE_ADMIN 접근 가능
                                .requestMatchers(
                                        "/auth/admin-role",
                                        "/users/read-business",
                                        "/users/approve-business/{userId}",
                                        "/users/reject-business/{userId}"
                                )
                                .hasRole("ADMIN")
                )
                // JWT를 사용하기 떄문에 보안 관련 세션 해제
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS)
                )
                // JWT 필터를 권한 필터 앞에 삽입
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenUtils, manager), AuthorizationFilter.class
                );
        return http.build();
    }

    //    @Bean
    // 사용자 정보 관리 클래스
    public UserDetailsManager userDetailsManager(
            PasswordEncoder passwordEncoder
    ) {
        // 사용자 1
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder.encode("password1"))
                .build();
        // Spring Security에서 기본으로 제공하는,
        // 메모리 기반 사용자 관리 클래스 + 사용자 1
        return new InMemoryUserDetailsManager(user1);
    }
}
