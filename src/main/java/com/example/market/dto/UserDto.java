package com.example.market.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;


@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String nickname;
    private String name;
    private int age;
    private String email;
    private String phoneNumber;
}