package com.example.market.shop.repo;

import com.example.market.shop.entity.Shop;
import com.example.market.shop.entity.ShopStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByOwnerId(Long id);

    List<Shop> findByShopStatus(ShopStatus shopStatus);
}
