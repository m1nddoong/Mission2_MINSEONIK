package com.example.market.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// UserEntity를 바탕으로 Spring Security 내부에서
// 사용자 정보를 주고받기 위한 객체임을 나타내는 interface UserDetails
// 의 커스텀 구현체
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String name;
    private Integer age;
    private String email;
    private String phone;
    private String authorities;
    private String avatar;
    private String businessNumber;
    @Getter
    private UserEntity entity;

    // userEntity 를 기반으로 CustomUserDetails 객체 생성
    public static CustomUserDetails fromEntity(UserEntity entity) {
        return CustomUserDetails.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .name(entity.getName())
                .age(entity.getAge())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .authorities(entity.getAuthorities())
                .avatar(entity.getAvatar())
                .businessNumber(entity.getBusinessNumber())
                .entity(entity)
                .build();
    }

    public String getRawAuthorities() {
        return this.authorities;
    }


    @Override
    // ROLE_USER,ROLE_ADMIN,READ_AUTHORITY,WRITE_AUTHORITY
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*return Arrays.stream(authorities.split(","))
                .sorted()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());*/

        List<GrantedAuthority> grantedAuthorities
                = new ArrayList<>();
        String[] rawAuthorities = authorities.split(",");
        for (String rawAuthority: rawAuthorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(rawAuthority));
        }

        return grantedAuthorities;
        // return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


    // 먼 미래
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}