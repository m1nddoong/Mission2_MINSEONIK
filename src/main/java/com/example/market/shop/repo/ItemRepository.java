package com.example.market.shop.repo;

import com.example.market.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository
        // 다중 상속 -> ProductQuerydslRepo
        extends JpaRepository<Item, Long>, ItemQuerydslRepo {

}
