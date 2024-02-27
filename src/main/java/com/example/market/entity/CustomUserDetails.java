package com.example.market.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.springframework.security.core.userdetails.User;
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
    private int age;
    private String email;
    private String phone;
    @Setter
    private String authorities;
    private String avatar;
    private String businessNumber;

    // OptionalUser 라는 레포지토리로부터 조회한 사용자 엔티티 정보를 가지고
    // CustomUserDetails형 객체로 변환하는 과정
    public static CustomUserDetails fromEntity(UserEntity entity) {
        CustomUserDetails details = new CustomUserDetails();
        details.id = entity.getId();
        details.username = entity.getUsername();
        details.password = entity.getPassword();
        details.nickname = entity.getNickname();
        details.email = entity.getEmail();
        details.phone = entity.getPhone();
        details.age = entity.getAge();
        details.authorities = entity.getAuthorities();
        details.avatar = entity.getAvatar();
        details.businessNumber = entity.getBusinessNumber();
        return details;
    }


    // 새로운 객체 생성 메서드
    public UserEntity newEntity() {
        UserEntity entity = new UserEntity();
        entity.setUsername(this.username);
        entity.setPassword(this.password);
        entity.setNickname(this.nickname);
        entity.setEmail(this.email);
        entity.setPhone(this.phone);
        entity.setAge(this.age);
        entity.setAuthorities(this.authorities);
        return entity;
    }


    // 기존 CustomUserDetails 클래스 내에서
//    public CustomUserDetails withAuthorities(String authorities) {
//        this.authorities = authorities;
//        return this;
//    }


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