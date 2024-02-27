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

    /**
     * userDetails 를 받아서 클레임으로 만들고, JWT로 변환하는 메서드
     * @param userDetails
     * @return
     */
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


    /**
     * 토큰을 검증하여 정상적인 JWT 인지 판단하는 메서드
     * @param token
     * @return
     */
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


    /**
     * JWT 토큰을 인자로 받고, 그것을 해석하여
     * 그 안에 포함된 JWT의 클레임(Claims - 실제 데이터(Payload))을 반환하는 메서드
     * @param token
     * @return
     */
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

}
