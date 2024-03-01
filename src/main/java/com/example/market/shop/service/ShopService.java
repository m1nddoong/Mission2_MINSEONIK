package com.example.market.shop.service;

import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import com.example.market.shop.dto.ShopDto;
import com.example.market.shop.entity.Shop;
import com.example.market.shop.entity.ShopCategory;
import com.example.market.shop.entity.ShopStatus;
import com.example.market.shop.repo.ShopRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        shop.setShopStatus(ShopStatus.PREPARING); // 준비중
        shop.setOwner(userEntity);
        shopRepository.save(shop);
    }


    // 본인이 쇼핑몰 업데이트 -> 인증된 사용자여야 함
    // 이름, 소개, 분류의 정보
    public void updateShop(ShopDto dto) {
        // 현재 인증된 사용자의 객체 가져오기
        UserEntity userEntity = getCurrentUser();
        // 쇼핑몰 정보 가져오기
        Shop shop = getCurrentUserShop(userEntity);

        // dto 로부터 쇼핑몰 정보 업데이트
        shop.setShopName(dto.getShopName());
        shop.setIntroduction(dto.getIntroduction());
        shop.setCategory(ShopCategory.valueOf(dto.getCategory()));

        // 저장
        shopRepository.save(shop);
    }


    public boolean applyOpening() {
        // 현재 인증된 사용자의 객체 가져오기
        UserEntity userEntity = getCurrentUser();
        // 쇼핑몰 정보 가져오기
        Shop shop = getCurrentUserShop(userEntity);

        // 이름, 소개, 분류가 모두 작성 되었다면
        if (shop.getShopName() != null && shop.getIntroduction() != null && shop.getCategory() != null) {
            // 쇼핑몰 개설을 신청할 수 있다.
            shop.setShopStatus(ShopStatus.OPEN_REQUESTED);
            shopRepository.save(shop);
            return true;
        }
        else {
            return false;
        }

    }

    public List<ShopDto> getAllOpenRequests() {
        // 오픈 요청된 Shop 엔티티 리스트들을 ShopDto로 변환
        return shopRepository.findByShopStatus(ShopStatus.OPEN_REQUESTED).stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());
    }


    /*
    중복을 피하기 위해 인증된 사용의 객체, 쇼핑몰 객체를 가져오는 부분을 따로 메서드로 추출
     */
    private UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }
        return optionalUserEntity.get();
    }

    private Shop getCurrentUserShop(UserEntity userEntity) {
        Optional<Shop> optionalShopEntity = shopRepository.findByOwnerId(userEntity.getId());
        if (optionalShopEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자의 쇼핑몰 정보를 찾을 수 없습니다.");
        }
        return optionalShopEntity.get();
    }
}
