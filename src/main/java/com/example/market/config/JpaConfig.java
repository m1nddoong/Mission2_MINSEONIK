package com.example.market.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
//@RequiredArgsConstructor
public class JpaConfig {
//    private final EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(
            EntityManager entityManager
    ) {
        return new JPAQueryFactory(entityManager);
    }
}