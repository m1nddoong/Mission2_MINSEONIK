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
@RequiredArgsConstructor
public class AuthorizationController {
    private final JwtTokenUtils jwtTokenUtils;
    private final JpaUserDetailsManager userDetailsManager;
    private final AuthorizationService authorizationService;

    // ROLE_REGULAR_USER 를 가졌을 때 요청 가능
    // 사업자 등록 번호를 전달해 사업자 사용자로 전환 신청
    @PostMapping("/regular-user-role")
    public String regularUserRole(
            @RequestBody UserDto dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // authorities를 사용하여 필요한 작업 수행
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("REGULAR_USER")) {
                    // ROLE_REGULAR_USER 권한이 있을 때의 처리
                    return "권한이 ROLE_REGULAR_USER 입니다";
                }
            }
        } else {
            // 사용자가 인증되지 않았거나 인증 정보가 없는 경우 처리
        }

        // ROLE_REGULAR_USER 권한이 없을 때의 처리
        // 사업자 등록 번호를 신청하여, 신청 목록에 기재 // 신청 목록은 ArrayList
        // 나중에 관리자가 이 신청 목록을 보고 수락 또는 거절할 수 있음
        // authorizationService.addToRegistrationList(dto.getBusinessNumber());
        // log.info("Business registration requested for: {}", dto.getBusinessNumber());
        // return ResponseEntity.ok("Registration request submitted successfully");
        return "권한이 ROLE_REGULAR_USER 이 아닙니다!!!!!";
    }



    // ROLE_ADMIN 을 가졌을 때 요청 가능
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
