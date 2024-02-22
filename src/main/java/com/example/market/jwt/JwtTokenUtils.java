package com.example.market.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// JWT 자체와 관련된 기능을 만드는 곳
@Slf4j
@Component
public class JwtTokenUtils {
    // JWT를 만드는 용도의 암호키
    private final Key signingKey;

    public JwtTokenUtils(
            @Value("${jwt.secret}")
            String jwtSecret
    ) {
        log.info(jwtSecret);
        this.signingKey
                = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // UserDetails 를 받아서 JWT로 변환하는 메서드
    public String generateToken(UserDetails userDetails) {
        // JWT에 담고싶은 정보를 Claims로 만든다.

        // 현재 호출되었을 때 epoch time
        Instant now = Instant.now();
        Claims jwtClaims = Jwts.claims()
                // sub : 누구인지
                .setSubject(userDetails.getUsername())
                // iat : 언제 발급 되었는지
                .setIssuedAt(Date.from(now))
                // exp : 언제 만료 예정인지
                .setExpiration(Date.from(now.plusSeconds(20L)));

        // 최종적으로 JWT를 발급한다.
        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(this.signingKey)
                .compact();
    }
}
