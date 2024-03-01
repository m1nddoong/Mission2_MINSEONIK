package com.example.market.product.service;


import static com.example.market.shop.entity.ShopStatus.OPEN;

import com.example.market.entity.UserEntity;
import com.example.market.product.dto.ProductDto;
import com.example.market.product.entity.Product;
import com.example.market.product.repo.ProductRepository;
import com.example.market.repo.UserRepository;
import com.example.market.shop.entity.Shop;
import com.example.market.shop.entity.ShopStatus;
import com.example.market.shop.repo.ShopRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public boolean createProduct(ProductDto dto) {
        // 현재 인증된 사용자 정보 가져오기
        UserEntity userEntity = getCurrentUser();
        // 쇼핑몰 정보 가져오기
        Shop shop = getCurrentUserShop(userEntity);

        if (shop.getShopStatus().equals(OPEN)) {
            // 쇼핑몰에 상품을 추가
            Product product = new Product();
            product.setName(dto.getName());
            product.setContent(dto.getContent());
            product.setPrice(dto.getPrice());
            product.setContent(dto.getCategory());
            product.setSubCategory(dto.getSubCategory());
            product.setStock(dto.getStock());
            product.setWriter(userEntity);

            // 저장
            productRepository.save(product);
            return true;
        }
        else {
            return false;
        }

    }


    /*
   <===================================================================>
   중복을 피하기 위해 인증된 사용의 객체, 쇼핑몰 객체를 가져오는 부분을 따로 메서드로 추출
   <===================================================================>
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

    private Shop getUserShopFromId(Long shopId) {
        Optional<Shop> optionalShopEntity = shopRepository.findById(shopId);
        if (optionalShopEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자의 쇼핑몰 정보를 찾을 수 없습니다.");
        }
        return optionalShopEntity.get();
    }

}
