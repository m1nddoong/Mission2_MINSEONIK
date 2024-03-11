package com.example.market.config;

import com.example.market.user.jwt.JwtTokenFilter;
import com.example.market.user.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

// @Bean을 비롯해서 인증 등 여러 설정을 하기 위한 Bean 객체
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    // 메서드의 결과를 Bean 객체로 관리해주는 어노테이션
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService manager;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http // 인증과 권한 관련 설정을 적용할 수 있는 객체
    ) throws Exception {
        http
                // csrf 보안 해제
                .csrf(AbstractHttpConfigurer::disable)
                // url에 따른 요청 인가
                .authorizeHttpRequests(auth -> auth
                        // 어떤 경로에 대한 설정인지
                        .requestMatchers(
                                "/users/signin",
                                "/token/validate",
                                "/users/details",
                                "/users/avatar",
                                "/users/signup",
                                "/",
                                "/home",
                                "/static/**",
                                "/static/item.html?id=1",
                                "/items"
                        )
                        // 이 경로에 도달할 수 있는 사람에 대한 설정 (모두)
                        .permitAll()

                        // ROLE_USER 이상 접근 가능
                        .requestMatchers(
                                "/auth/user-role",
                                "/users/register-business",
                                "/used-items",
                                "/used-items/register",
                                "/used-items/register-image",
                                "/used-items/update/{itemId}",
                                "/used-items/delete/{itemId}",
                                "/used-items/{itemId}/proposals",
                                "/used-items/{itemId}/proposals/{proposalId}/accept",
                                "/used-items/{itemId}/proposals/{proposalId}/reject",
                                "/used-items/{itemId}/proposals/{proposalId}/confirm",
                                "/shop/read-all-shops",
                                "/items/search",
                                "/orders/purchase-request",
                                "/orders/purchase-approve/{itemOrderId}",
                                "/orders/purchase-reject/{itemOrderId}",
                                "/orders/purchase-cancel/{itemOrderId}"
                        )
                        .hasAnyRole("USER", "BUSINESS_USER", "ADMIN")

                        // ROLE_BUSINESS_USER 이상부터 접근 가능
                        .requestMatchers(
                                "/shop/update",
                                "/shop/open-request",
                                "/shop/close-request",
                                "/items/create",
                                "/items/{productId}/update",
                                "/items/{productId}/delete"
                        )
                        .hasAnyRole("BUSINESS_USER", "ADMIN")

                        // ROLE_ADMIN 만 접근 가능
                        .requestMatchers(
                                "/auth/admin-role",
                                "/users/read-business",
                                "/users/approve-business/{userId}",
                                "/users/reject-business/{userId}",
                                "/shop/get-all-open-requests",
                                "/shop/approve-open/{shopId}",
                                "/shop/reject-open/{shopId}",
                                "/shop/get-all-close-requests",
                                "/shop/approve-close/{shopId}"
                        )
                        .hasRole("ADMIN")
                )

                // JWT를 사용하기 떄문에 보안 관련 세션 해제
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // JWT 필터를 권한 필터 앞에 삽입
                .addFilterBefore(
                        new JwtTokenFilter(
                                jwtTokenUtils,
                                manager
                        ),
                        AuthorizationFilter.class
                );
        return http.build();
    }
}
