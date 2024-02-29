package com.example.market.controller;

import com.example.market.dto.UserDto;
import com.example.market.entity.CustomUserDetails;
import com.example.market.entity.UserEntity;
import com.example.market.jwt.JwtTokenUtils;
import com.example.market.repo.UserRepository;
import com.example.market.service.JpaUserDetailsManager;
import com.example.market.service.UserService;
import com.example.market.shop.service.ShopService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final JpaUserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ShopService shopService;


    /**
     * 회원가입
     * @param dto 사용자의 username, password
     * @return 새로운 사용자를 생성 후 DB 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(
            @RequestBody
            UserDto dto
    ) {
        if (manager.userExists(dto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자가 이미 존재합니다.");
        }

        // UserEntity 객체 생성
        UserEntity userEntity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .authorities("ROLE_INACTIVE_USER")
                .build();

        // 저장
        userService.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 성공!");
    }


    /**
     * 회원정보 추가
     * @param dto 추가정보가 담긴 UserDto
     * @return 추가 정보 업데이트
     */
    @PostMapping("/profile-info")
    public ResponseEntity<String> profileInfo(
            @RequestBody UserDto dto
    ) {
        // 현재 인증된 사용자의 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 데이터베이스에서 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isPresent()) {
            // 업데이트할 정보 설정
            UserEntity userEntity = optionalUserEntity.get();
            userEntity.setNickname(dto.getNickname());
            userEntity.setName(dto.getName());
            userEntity.setAge(dto.getAge());
            userEntity.setEmail(dto.getEmail());
            userEntity.setPhone(dto.getPhone());
            userEntity.setAuthorities("ROLE_USER"); // 일반 사용자로 승급
            userRepository.save(userEntity);

            return ResponseEntity.status(HttpStatus.OK).body("프로필 추가 정보 작성 완료!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한 없음");
        }
    }


    /**
     * 사용자 프로필 이미지 업로드
     *
     * @param multipartFile 이미지 파일
     * @return 이미지 파일 업로드
     * @throws IOException transferTo 로 저장
     */
    @PostMapping(
            value = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> avatar(
            @RequestParam("file")
            MultipartFile multipartFile
    ) throws IOException {
        // 현재 인증된 사용자의 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("디렉토리 생성 실패");
            }
            // 파일 조합: media/{username}/profile.{확장자}
            String originalFilename = multipartFile.getOriginalFilename();
            String[] fileNameSplit = originalFilename.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            String profileFilename = "profile." + extension;
            String profilePath = profileDir + profileFilename;
            log.info(profileFilename);

            multipartFile.transferTo(Path.of(profilePath));
            log.info(profilePath);

            // 업로드된 파일의 URL 저장
            String requestPath = String.format("/media/%s/%s", username, profileFilename);
            userEntity.setAvatar(requestPath);
            userRepository.save(userEntity);
            // 성공적으로 업로드된 메시지 반환
            return ResponseEntity.status(HttpStatus.OK).body("프로필 이미지가 업로드 되었습니다.");
        } else {
            // 사용자를 찾을 수 없을 경우 오류 메시지 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * 사업자 등록번호 신청 (ROLE_USER 일떄만 가능)
     * @param dto businessNumber 만 포함
     * @return 해당 사용자의 사업자 등록번호 컬럼에 데이터 추가
     */
    @PostMapping("/register-business")
    public ResponseEntity<String> registerBusinessUser(
            @RequestBody
            UserDto dto
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            // 자신의 사업자 번호 등록 (데이터베이스에)
            userEntity.setBusinessNumber(dto.getBusinessNumber());
            userRepository.save(userEntity);
            return ResponseEntity.ok("사업자 등록번호 신청 완료");
        } else {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * 사업자 등록 번호가 존재하는 사용자 정보 반환
     * @return 사용자 정보 반환
     */
    @GetMapping("/read-business")
    public ResponseEntity<List<UserEntity>> getBusinessUserRequest() {
        List<UserEntity> businessUserRequests = userRepository.findByBusinessNumberNotNull();
        return ResponseEntity.ok(businessUserRequests);
    }


    /**
     * 사업지 사용자 신청 승인
     * @param userId
     * @return
     */
    @PostMapping("/approve-business/{userId}")
    public ResponseEntity<String> approveBusinessUserRequest(
            @PathVariable
            Long userId
    ) {
        // 사용자의 정보 가져오기
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();

            // 사업자 사용자 권한으로 설정
            userEntity.setAuthorities("ROLE_BUSINESS_USER");
            userRepository.save(userEntity);

            // PREPARING 상태인 쇼핑몰이 생성이 되어,
            // shop 레포지토리에 추가된다.
            shopService.registerShop(userEntity);

            return ResponseEntity.ok("사업자 사용자 등록이 완료되었고, 쇼핑몰이 추가되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * 사업자 사용자 신청 거절 후 데이터베이스에서 해당 사용자의 사업자 번호 삭제
     * @param userId
     * @return
     */
    @PostMapping("/reject-business/{userId}")
    public ResponseEntity<String> rejectBusinessUserRequest(
            @PathVariable
            Long userId
    ) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            userEntity.setBusinessNumber(null);
            userRepository.save(userEntity);
            return ResponseEntity.ok("사업자 사용자 등록이 거절되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }
    }


}