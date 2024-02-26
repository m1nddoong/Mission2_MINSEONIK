package com.example.market.service;

import com.example.market.dto.UserDto;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }


    public void updateUserProfile(
        String username,
        UserDto dto
    ) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        log.info("Username extracted from JWT token: {}", username);
        if (optionalUser.isEmpty()) {
            // 사용자가 존재하지 않는 경우 예외 처리
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // 사용자가 존재하는 경우 프로필 정보 업데이트
        UserEntity userEntity = optionalUser.get();
        userEntity.setNickname(dto.getNickname());
        userEntity.setNickname(dto.getName());
        userEntity.setAge(dto.getAge());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPhoneNumber(dto.getPhoneNumber());

        userRepository.save(userEntity);
    }

    // ResponseEntity 타입은 HTTP 응답을 좀 더 상세하게 제어하고
    // 다양한 상태 코드와 본문을 클라이언트에게 반환하기 위함.

    // 일반적으로 @RestController 가 붙은 컨트롤러에서 메서드가 HTTP 요청에 대한 응답을 반환할떄
    // ResponseEntity를 사용한다.
}
