package com.example.market.controller;

import com.example.market.dto.UserDto;
import com.example.market.entity.UserEntity;
import com.example.market.jwt.JwtTokenUtils;
import com.example.market.repo.UserRepository;
import com.example.market.service.UserService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Transactional
public class UserController {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserRepository userRepository;


    // 아이디와 비밀번호를 제공하여 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestBody
            UserDto dto
    ) {
        if (userDetailsManager.userExists(dto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exist");
        }

        // UserDetails 객체 생성
        UserEntity userEntity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                // 최초 회원가입 시 비활성 사용자로 가입
                .authorities("ROLE_USER")
                .build();

        // 사용자 등록
        userService.save(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered success");
    }

    // 서비스 이용을 위한 정보 추가
    @PostMapping("/profile-info")
    public ResponseEntity<String> profileInfo(
           @RequestBody
           UserDto dto,
           @RequestHeader("Authorization")
           String token
    ) {
        // JWT 토큰에서 사용자 이름 추출
        String username = jwtTokenUtils.getUsernameFromToken(token);

        userService.updateUserProfile(username, dto);
        return ResponseEntity.status(HttpStatus.OK).body("Profile information successfully");
    }



    // 사용자의 이미지 업로드
    @PostMapping(
            value = "/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE // multipart 요청임을 나타냄.
    )
    public String uploadProfileImage(
            @RequestParam("file")
            MultipartFile multipartFile,
            @RequestHeader("Authorization")
            String token
    ) throws IOException {
        log.info(multipartFile.getOriginalFilename());

        // JWT 토큰에서 사용자 이름 추출
        String username = jwtTokenUtils.getUsernameFromToken(token);

        // 사용자 정보 업데이트
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 파일을 저장할 '경로 + 파일명' 지정 -> 위치를 나타내는 객체 역할
        Path downloadPath = Path.of("media/" + multipartFile.getOriginalFilename());
        // 저장한다.
        multipartFile.transferTo(downloadPath);
        return "done";
    }


}
