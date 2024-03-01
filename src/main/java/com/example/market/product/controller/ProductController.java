package com.example.market.product.controller;


import com.example.market.product.dto.ProductDto;
import com.example.market.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    // 쇼핑몰 주인은 쇼핑몰에 상품을 등록할 수 있다.
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @RequestBody
            ProductDto dto
    ) {
        if (service.createProduct(dto)) {
            return ResponseEntity.ok("쇼핑몰에 상품을 등록하였습니다.");
        } else {
            return ResponseEntity.ok("쇼핑몰이 개설되지 않았거나, 상품 등록에 실패하였습니다.");
        }
    }

}
