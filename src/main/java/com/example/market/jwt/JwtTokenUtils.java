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
    // JWT를 해석하는 용도의 객체
    private final JwtParser jwtParser;

    public JwtTokenUtils(
            @Value("${jwt.secret}")
            String jwtSecret
    ) {
        log.info(jwtSecret);
        this.signingKey
                = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
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
                .setExpiration(Date.from(now.plusSeconds(60 * 60 * 24 * 7)));

        // 최종적으로 JWT를 발급한다.
        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(this.signingKey)
                .compact();
    }

    // 정상적인 JWT인지 판단하는 메서드
    public boolean validate(String token) {
        try {
            // 정상적이지 않은 JWT라면 예외(Exception)가 발생한다.
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("invalid jwt");
        }
        return false;
    }

    // JWT를 인자로 받고, 그 JWT를 해석해서 그 안에 포함된 JWT의 클레임(Claims)을 가져오는 메서드
    // 실제 데이터(Payload)를 반환하는 메서드
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }


    // JWT 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        try {
            // JWT 토큰 해석하여 클레임 추출
            Claims claims = parseClaims(token);

            // 클레임에서 사용자 이름 추출
            return claims.getSubject();
        } catch (Exception e) {
            // 오류 처리
            return null;
        }
    }
}
