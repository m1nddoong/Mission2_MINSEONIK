package com.example.market.user.dto;

import com.example.market.user.entity.UserEntity;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Builder
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String nickname;
    private String name;
    private Integer age;
    private String email;
    private String phone;
    private String authorities;
    private String avatar;
    private String businessNumber;

    // 추가
    public static UserDto fromEntity(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nickname(entity.getNickname())
                .name(entity.getName())
                .age(entity.getAge())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .authorities(entity.getAuthorities())
                .avatar(entity.getAvatar())
                .businessNumber(entity.getBusinessNumber())
                .build();
    }
}