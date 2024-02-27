package com.example.market.service;


import com.example.market.entity.RegistrationEntity;
import com.example.market.repo.RegistrationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final RegistrationRepository registrationRepository;

    //  신청 목록에 사업자 등록 번호 추가
    public void addToRegistrationList(String businessNumber) {
        RegistrationEntity registrationEntity = new RegistrationEntity();
        registrationEntity.setBusinessNumber(businessNumber);
        registrationRepository.save(registrationEntity);
    }

    // 신청 목록 조회
    public List<RegistrationEntity> getRegistrationList() {
        return registrationRepository.findAll();
    }
}
