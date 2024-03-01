package com.example.market.shop.service;

import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import com.example.market.shop.dto.ClosureRequestDto;
import com.example.market.shop.dto.EmailDto;
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
// RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ShopService(
            ShopRepository shopRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;

        // 테스트용 데이터 가져오기
        // BusinessUser
        Optional<UserEntity> userEntity = userRepository.findById(2L);
        if (userEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

        Shop shop = Shop.builder()
                .shopName("향수 공방")
                .introduction("향수 공방 제품 판매합니다.")
                .category(ShopCategory.BEAUTY)
                .owner(userEntity.get())
                .shopStatus(ShopStatus.OPEN)
                .build();
        shopRepository.save(shop);

        // USER1
        userEntity = userRepository.findById(3L);
        if (userEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

        shop = Shop.builder()
                .shopName("PC Factory")
                .introduction("직수입 할인 매장 운영합니다. 반갑습니다.")
                .category(ShopCategory.ELECTRONICS)
                .owner(userEntity.get())
                .shopStatus(ShopStatus.OPEN)
                .build();
        shopRepository.save(shop);

        // USER2
        userEntity = userRepository.findById(4L);
        if (userEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

        shop = Shop.builder()
                .shopName("FASHION village")
                .introduction("안녕하세요. 수제 의류 용퓸 제작 판매합니다.")
                .category(ShopCategory.FASHION)
                .owner(userEntity.get())
                .shopStatus(ShopStatus.OPEN)
                .build();
        shopRepository.save(shop);



    }

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

    // 쇼핑몰 개설 신청
    public boolean requestShopOpen() {
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
        } else {
            return false;
        }

    }

    // 개설 신청된 쇼핑몰 목록 확인
    public List<ShopDto> getAllOpenRequests() {
        // 오픈 요청된 Shop 엔티티 리스트들을 ShopDto로 변환
        return shopRepository.findByShopStatus(ShopStatus.OPEN_REQUESTED).stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());
    }


    // 쇼핑몰 개설 신청 허가
    public void approveOpen(Long shopId) {
        // 쇼핑몰 정보 가져오기
        Shop shop = getUserShopFromId(shopId);
        shop.setShopStatus(ShopStatus.OPEN);
        shopRepository.save(shop);
    }

    // 쇼핑몰 개설 신청 불허, 불허 사유 전송하기
    public void rejectOpen(Long shopId, EmailDto dto) {
        // 쇼핑몰 정보 가져오기
        Shop shop = getUserShopFromId(shopId);
        // 오픈 요청 거절
        shop.setShopStatus(ShopStatus.OPEN_REQUEST_REJECTED);
        shopRepository.save(shop);

        // 쇼핑몰 주인의 이메일 주소
        String ownerEmail = shop.getOwner().getEmail();

        // 이메일 전송
        emailService.sendEmail(ownerEmail, dto);
    }

    // 쇼핑몰 폐쇄 요청
    public boolean requestShopClosure(ClosureRequestDto dto) {
        // 현재 인증된 사용자의 객체 가져오기
        UserEntity userEntity = getCurrentUser();
        // 쇼핑몰 정보 가져오기
        Shop shop = getCurrentUserShop(userEntity);

        // 폐쇄 요청
        shop.setShopStatus(ShopStatus.CLOSED_REQUESTED);
        shopRepository.save(shop);

        // 콘솔에 사유 출력 (폐쇄 요청 사유를 어디에 보여지게 할지가 의문이다..)
        log.info(shop.getOwner().getUsername() + "님의 쇼핑몰 폐쇄 요청 사유: " + dto.getText());
        return true;
    }


    // 관리자는 쇼핑몰 페쇄 요청들을 확인
    public List<ShopDto> getAllCloseRequests() {
        // 폐쇄 요청된 Shop 엔티티 리스트들을 ShopDto 로 변환
        return shopRepository.findByShopStatus(ShopStatus.CLOSED_REQUESTED).stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());

    }


    // 쇼핑몰 폐쇄 신청 허가
    public void approveClose(Long shopId) {
        // 쇼핑몰 정보 가져오기
        Shop shop = getUserShopFromId(shopId);
        shop.setShopStatus(ShopStatus.CLOSED);
        shopRepository.save(shop);
    }


    /*
    <===================================================================>
    중복을 피하기 위해 사용자 객체, 쇼핑몰 객체를 가져오는 부분을 따로 메서드로 추출
    <===================================================================>
     */

    // 사용자의 인증 정보를 가져올 메서드
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

    private Shop getUserShopFromId(Long shopId) {
        Optional<Shop> optionalShopEntity = shopRepository.findById(shopId);
        if (optionalShopEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자의 쇼핑몰 정보를 찾을 수 없습니다.");
        }
        return optionalShopEntity.get();
    }


}
