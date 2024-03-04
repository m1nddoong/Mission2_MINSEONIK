package com.example.market.shop.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class TossOrderController {
    private final TossOrderService service;

//    @PostMapping("/purchase")
//    public ResponseEntity<String> purchaseItem(
//            @RequestBody ItemOrderDto dto
//    ) {
//
//    }
}