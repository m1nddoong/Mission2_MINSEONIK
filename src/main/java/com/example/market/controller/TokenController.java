package com.example.market.controller;

import com.example.market.jwt.JwtRequestDto;
import com.example.market.jwt.JwtResponseDto;
import com.example.market.jwt.JwtTokenUtils;
import com.example.market.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    // JWT를 발급하기 위한 클래스
    private final JwtTokenUtils jwtTokenUtils;
    // 사용자 정보를 회수하기 위한 Bean
    private final UserDetailsManager manager;
    // 사용자가 제공한 아이디 비밀번호를 비교하기 위한 클래스
    private final PasswordEncoder passwordEncoder;

    // 프로필 이미지를 저장하고 업데이트하기 위해
    private final UserRepository userRepository;


    // POST /token/issue
    @PostMapping("/issue")
    public JwtResponseDto issueJwt(
            @RequestBody JwtRequestDto dto
    ) {
        // 사용자가 제공한 username, password가 저장된 사용자인지
        if (!manager.userExists(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserDetails userDetails
                = manager.loadUserByUsername(dto.getUsername());

        // passwordEncoder.matches(rawPassword, encodedPassword)
        // 평문 비밀번호와 암호화 비밀번호를 비교할 수 있다.
        if (!passwordEncoder
                .matches(dto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // JWT 발급
        String jwt = jwtTokenUtils.generateToken(userDetails);
        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwt);
        return response;
    }

//    @GetMapping("/validate")
//    public Claims validateToken(
//            @RequestParam("token")
//            String token
//    ) {
//        if (!jwtTokenUtils.validate(token))
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//
//        return jwtTokenUtils.parseClaims(token);
//    }
}
