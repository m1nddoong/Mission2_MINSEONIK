package com.example.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Builder
@Setter
@AllArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String nickname;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String authorities;
    private String avatar;
    private String businessNumber;
}