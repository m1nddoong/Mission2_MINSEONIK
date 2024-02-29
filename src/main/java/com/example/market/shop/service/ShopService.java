package com.example.market.shop.service;

import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import com.example.market.shop.dto.ShopDto;
import com.example.market.shop.entity.Shop;
import com.example.market.shop.entity.ShopCategory;
import com.example.market.shop.entity.ShopStatus;
import com.example.market.shop.repo.ShopRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public void registerShop(
            UserEntity userEntity
    ) {
        Shop shop = new Shop();
        shop.setStatus(ShopStatus.PREPARING); // 준비중
        shop.setOwner(userEntity);
        shopRepository.save(shop);
    }


    // 본인이 쇼핑몰 업데이트 -> 인증된 사용자여야 함
    // 이름, 소개, 분류의 정보
    public void updateShop(ShopDto dto) {
        // 현재 인증된 사용자의 객체 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get();


        // 쇼핑몰 정보 가져오기
        Optional<Shop> optionalShopEntity = shopRepository.findByOwnerId(userEntity.getId());
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자의 쇼핑몰 정보를 찾을 수 없습니다.");
        Shop shop = optionalShopEntity.get();

        // dto 로부터 쇼핑몰 정보 업데이트
        shop.setShopName(dto.getShopName());
        shop.setIntroduction(dto.getIntroduction());
        shop.setCategory(ShopCategory.valueOf(dto.getCategory()));

        // 저장
        shopRepository.save(shop);
    }
}
