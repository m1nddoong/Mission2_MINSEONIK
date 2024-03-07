package com.example.market.user.service;

import com.example.market.user.controller.AuthenticationFacade;
import com.example.market.user.dto.CreateUserDto;
import com.example.market.user.dto.UpdateUserDto;
import com.example.market.user.dto.UserDto;
import com.example.market.user.entity.CustomUserDetails;
import com.example.market.user.entity.UserEntity;
import com.example.market.user.jwt.JwtRequestDto;
import com.example.market.user.jwt.JwtResponseDto;
import com.example.market.user.jwt.JwtTokenUtils;
import com.example.market.user.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthenticationFacade authFacade;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    // 기본 오버라이드 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::fromEntity)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }

    /**
     * 회원가입
     */
    @Transactional
    public UserDto createUser (
            // UserDto 에서 CreateUserDto 로 변경 -> passwrodCheck 라는 필드 포함
            CreateUserDto dto
    ) {
        if (!dto.getPassword().equals(dto.getPasswordCheck())) // 비밀번호와 비밀번호 확인이 다르면
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (userRepository.existsByUsername(dto.getUsername())) // 데이터베이스에 기존 사용자 존재 유뮤 판단
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

//        마찬가지로 사용자 엔티티의 여부를 UserDetailManager 가 아닌 UserRepository 에서 확인함
//        if (manager.userExists(dto.getUsername())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자가 이미 존재합니다.");
//        }

        return UserDto.fromEntity(userRepository.save(UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build()));

        // UserEntity 객체 생성
//        UserEntity userEntity = UserEntity.builder()
//                .username(dto.getUsername())
//                .password(passwordEncoder.encode(dto.getPassword()))
//                .authorities("ROLE_INACTIVE_USER")
//                .build();
        // 저장
        // userService.save(userEntity);
        // return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 성공!");
    }



    /**
     * 로그인 (JWT 토큰 발급)
     */
    public JwtResponseDto signin(
            @RequestBody JwtRequestDto dto
    ) {
        // 데이터베이스에 사용자가 존재하는지
        UserEntity userEntity = userRepository.findByUsername(dto.getUsername())
                // Optional.orElseThrow() : 값이 존재하지 않는 경우 예외 발생
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // passwordEncoder.matches(rawPassword, encodedPassword)
        // 평문 비밀번호와 암호화 비밀번호를 비교할 수 있다.
        if (!passwordEncoder.matches(
                dto.getPassword(),
                // userDetails.getPassword()))
                userEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        // JWT 발급
        String jwt = jwtTokenUtils.generateToken(CustomUserDetails.fromEntity(userEntity));
        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwt);
        return response;
    }

    /**
     * 회원 정보 추가
     */
    @Transactional
    public UserDto profileInfo(UpdateUserDto dto) {
        UserEntity userEntity = authFacade.extractUser();
        // 업데이트할 정보 설정
        userEntity.setAge(dto.getAge());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPhone(dto.getPhone());
        userEntity.setAuthorities("ROLE_USER"); // 일반 사용자로 승급

        return UserDto.fromEntity(userRepository.save(userEntity));
    }

    // 데이터베이스에 저장
    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

}
