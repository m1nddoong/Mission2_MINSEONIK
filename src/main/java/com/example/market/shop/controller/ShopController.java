package com.example.market.shop.controller;

import com.example.market.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShopController {
    private final ShopService service;



    // 준비중 상태인 쇼핑몰이 딱 생성되고 거기에 owner_id 는 사용자의 id 가 되도록


    // 그런 상태라면, 쇼핑몰 개설 신청


}
