package com.example.market.controller;

import com.example.market.dto.UserDto;
import com.example.market.entity.UserEntity;
import com.example.market.jwt.JwtTokenUtils;
import com.example.market.repo.UserRepository;
import com.example.market.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
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
            HttpServletRequest request,
            @RequestBody UserDto dto
    ) {
        // JWT 토큰에서 사용자 이름 추출
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 2. Authorization 헤더가 존재하는지 + Bearer로 시작하는지
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1];
            // Token이 유효한 토큰인지 -> 이미 validate 에서 유효성 검증하고, 시큐리티컨텍스트에도 추가해줬음
            // 사용자 정보 회수
            String username = jwtTokenUtils
                    .parseClaims(token)
                    .getSubject();

            // 데이터베이스에서 사용자 정보 가져오기
            Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
            if (optionalUserEntity.isPresent()) {
                // 업데이트할 정보 설정
                UserEntity userEntity = optionalUserEntity.get();
                userEntity.setNickname(dto.getNickname());
                userEntity.setName(dto.getName());
                userEntity.setAge(dto.getAge());
                userEntity.setEmail(dto.getEmail());
                userEntity.setPhoneNumber(dto.getPhoneNumber());
                userEntity.setAuthorities("일반 사용자");

                userRepository.save(userEntity);

                return ResponseEntity.status(HttpStatus.OK).body("Profile information successfully updated");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
    }

    @PostMapping(
            value = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> avatar(
            HttpServletRequest request,
            @RequestParam("file")
            MultipartFile multipartFile
    ) throws IOException {
        // JWT 토큰에서 사용자 이름 추출
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 2. Authorization 헤더가 존재하는지 + Bearer로 시작하는지
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1];
            // 사용자 정보 회수
            String username = jwtTokenUtils.parseClaims(token).getSubject();

            // 데이터베이스에서 사용자 정보 가져오기
            Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                // 폴더 경로 생성: media/{username}/
                String profileDir = String.format("media/%s/", username);
                try {
                    // 폴더 생성
                    Files.createDirectories(Path.of(profileDir));
                } catch (IOException e) {
                    // 폴더를 만드는데 실패하면 기록을 하고 사용자에게 알림
                    log.error(e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create directory");
                }
                // 파일 업로드: media/{username}/profile.{확장자}
                String originalFilename = multipartFile.getOriginalFilename();
                String[] fileNameSplit = originalFilename.split("\\.");
                String extension = fileNameSplit[fileNameSplit.length - 1];
                String profileFilename = "profile." + extension;
                String profilePath = profileDir + profileFilename;
                log.info(profileFilename);

                multipartFile.transferTo(Path.of(profilePath));
                log.info(profilePath);

                // 업로드된 파일의 URL 저장
                String requestPath = String.format("/static/%s/%s", username, profileFilename);
                userEntity.setAvatar(requestPath);
                userRepository.save(userEntity);
                // 성공적으로 업로드된 메시지 반환
                return ResponseEntity.status(HttpStatus.OK).body("Avatar uploaded successfully");
            }
        }
        else {
            // 사용자를 찾을 수 없을 경우 오류 메시지 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }
}