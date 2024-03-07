//package com.example.market.user.service;
//
//
//import com.example.market.user.entity.CustomUserDetails;
//import com.example.market.user.entity.UserEntity;
//import com.example.market.user.repo.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.UserDetailsManager;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Optional;
//
//@Slf4j
////@RequiredArgsConstructor
//@Service
//// UserDetailsManager는 UserDetailsService 를 상속받은 인터페이스
//public class JpaUserDetailsManager implements UserDetailsManager {
//    private final UserRepository userRepository;
//
//    public JpaUserDetailsManager(
//            UserRepository userRepository,
//            PasswordEncoder passwordEncoder
//    ) {
//        this.userRepository = userRepository;
//        // 오롯이 테스트 목적의 사용자를 추가하는 용도
//        createUser(CustomUserDetails.builder()
//                .username("Admin")
//                .password(passwordEncoder.encode("1234"))
//                .nickname("햋님")
//                .name("관리자")
//                .age(40)
//                .email("admin@gmail.com")
//                .phone("010-9999-5555")
//                .authorities("ROLE_ADMIN")
//                .businessNumber("00000000")
//                .build());
//
//        createUser(CustomUserDetails.builder()
//                .username("BusinessUser")
//                .password(passwordEncoder.encode("1234"))
//                .nickname("보라돌이")
//                .name("사업자")
//                .age(31)
//                .email("businessUser1@gmail.com")
//                .phone("010-1111-2222")
//                .authorities("ROLE_BUSINESS_USER")
//                .businessNumber("20240301")
//                .build());
//
//        createUser(CustomUserDetails.builder()
//                .username("User1")
//                .password(passwordEncoder.encode("1234"))
//                .nickname("뚜비")
//                .name("사용자1")
//                .age(25)
//                .email("m1nddoong0321@gmail.com")
//                .phone("010-3333-4444")
//                .authorities("ROLE_BUSINESS_USER")
//                .businessNumber("20240226")
//                .build());
//
//        createUser(CustomUserDetails.builder()
//                .username("User2")
//                .password(passwordEncoder.encode("1234"))
//                .nickname("나나")
//                .name("사용자2")
//                .age(16)
//                .email("user2@gmail.com")
//                .phone("010-7777-1111")
//                .authorities("ROLE_BUSINESS_USER")
//                .businessNumber("20240229")
//                .build());
//
//        createUser(CustomUserDetails.builder()
//                .username("User3")
//                .password(passwordEncoder.encode("1234"))
//                .nickname("뽀")
//                .name("사용자3")
//                .age(9)
//                .email("user3@gmail.com")
//                .phone("010-7777-1111")
//                .authorities("ROLE_USER")
//                .build());
//
//        createUser(CustomUserDetails.builder()
//                .username("InactiveUser")
//                .password(passwordEncoder.encode("1234"))
//                .nickname("비활성 사용자")
//                .authorities("ROLE_INACTIVE_USER")
//                .build());
//    }
//
//
//
//
//    @Override
//    public boolean userExists(String username) {
//        return userRepository.existsByUsername(username);
//    }
//
//
//
//
//    // 나중에...
//    @Override
//    public void updateUser(UserDetails user) {
//        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
//    }
//
//    @Override
//    public void deleteUser(String username) {
//        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
//    }
//
//    @Override
//    public void changePassword(String oldPassword, String newPassword) {
//        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
//    }
//}