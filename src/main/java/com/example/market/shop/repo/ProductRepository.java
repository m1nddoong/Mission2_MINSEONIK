package com.example.market.shop.repo;

import com.example.market.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        // 다중 상속 -> ProductQuerydslRepo
        extends JpaRepository<Product, Long>, ProductQuerydslRepo {

}
