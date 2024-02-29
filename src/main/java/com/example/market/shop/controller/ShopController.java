package com.example.market.shop.controller;

import com.example.market.shop.dto.ShopDto;
import com.example.market.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService service;

    /**
     * 쇼핑몰 정보 수정
     * @param dto
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateShop(
            @RequestBody
            ShopDto dto
    ) {
        service.updateShop(dto);
        return ResponseEntity.ok("쇼핑몰 정보를 수정했습니다.");
    }






}
