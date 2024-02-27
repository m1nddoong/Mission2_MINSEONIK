package com.example.market.controller;

import com.example.market.dto.UserDto;
import com.example.market.jwt.JwtTokenUtils;
import com.example.market.service.AuthorizationService;
import com.example.market.service.JpaUserDetailsManager;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    // ROLE_USER를 가졌을 때 요청 가능
    @GetMapping("/user-role")
    public String userRole(
            @RequestBody
            UserDto dto
    ) {
        return dto.getBusinessNumber();
    }

    // ROLE_ADMIN을 가졌을 때 요청 가능
    @GetMapping("/admin-role")
    public String adminRole() {
        return "adminRole";
    }

    @GetMapping("/read-authority")
    public String readAuthority() {
        return "readAuthority";
    }

    @GetMapping("/write-authority")
    public String writeAuthority() {
        return "writeAuthority";
    }
}