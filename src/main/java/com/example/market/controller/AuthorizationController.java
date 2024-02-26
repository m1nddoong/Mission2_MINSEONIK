package com.example.market.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    // ROLE_USER 를 가졌을 때 요청 가능
    @GetMapping("/user-role")
    public String userRole() {
        return "userRole";
    }

    // ROLE_ADMIN 을 가졌을 때 요청 가능
    @GetMapping("/user-role")
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
