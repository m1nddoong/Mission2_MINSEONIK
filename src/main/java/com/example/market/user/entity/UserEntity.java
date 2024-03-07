package com.example.market.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity { // Spring 에 저장하고 싶은 사용자 정보를 일부 추가하여 만든다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;

//    @Setter
//    private String nickname;
//    @Setter
//    private String name;
    @Setter
    private int age;
    @Setter
    private String email;
    @Setter
    private String phone;

    // 테스트를 위해서 문자열 하나에 ','로 구분해 권한을 묘사
    // ROLE_USER,ROLE_ADMIN,READ_AUTHORITY,WRITE_AUTHORITY
    @Setter
    @Builder.Default
    private String authorities = "ROLE_INACTIVE";

    @Setter
    private String avatar;

    @Setter
    // 사업자 등록번호
    private String businessNumber;
}
