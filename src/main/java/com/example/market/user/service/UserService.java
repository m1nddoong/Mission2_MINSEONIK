package com.example.market.user.service;

import com.example.market.user.entity.UserEntity;
import com.example.market.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 데이터베이스에 저장
    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }


    // ResponseEntity 타입은 HTTP 응답을 좀 더 상세하게 제어하고
    // 다양한 상태 코드와 본문을 클라이언트에게 반환하기 위함.

    // 일반적으로 @RestController 가 붙은 컨트롤러에서 메서드가 HTTP 요청에 대한 응답을 반환할떄
    // ResponseEntity를 사용한다.
}
