package com.example.market.shop.service;

import com.example.market.entity.UserEntity;
import com.example.market.shop.entity.Shop;
import com.example.market.shop.entity.ShopStatus;
import com.example.market.shop.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public void createShop(
            UserEntity userEntity
    ) {
        Shop shop = new Shop();
        shop.setStatus(ShopStatus.PREPARING); // 준비중
        shop.setOwner(userEntity);
        shopRepository.save(shop);
    }
}
