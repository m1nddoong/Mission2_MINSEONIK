package com.example.market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 지금은 테스트 목적 의존성 때문에 문제가 발생해서 빼두었습니다. 실제로는 UserDetailsManager가
// PasswordEncoder를 필요로하지 않기 때문에 분리할 필요가 없습니다.
@Configuration
// Bean 객체의 순환 참조는 허용되지 않기 때문에, 순환 참조 문제가 발생할 경우 지금처럼 바깥으로 빼주시면 됩니다
public class PasswordEncoderConfig {
    @Bean
    // 비밀번호 암호화 클래스
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}