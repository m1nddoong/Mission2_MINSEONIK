package com.example.market.shop.controller;

import com.example.market.shop.dto.ItemOrderDto;
import com.example.market.shop.dto.PaymentCancelDto;
import com.example.market.shop.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemOrderController {
    private final ItemOrderService service;


    // 상품 구매 요청
    @PostMapping("/purchase-request")
    public ResponseEntity<String> purchaseItem(
            @RequestBody ItemOrderDto dto
    ) {
        try {
            service.purchaseItem(dto);
            return ResponseEntity.ok("구매 요청이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 쇼핑몰 주인의 구매 요청 수락 (주문 id 입력)
    @PostMapping("/purchase-approve/{itemOrderId}")
    public ResponseEntity<String> approveItem(
            @PathVariable
            Long itemOrderId
    ) {
        try {
            service.approveItem(itemOrderId);
            return ResponseEntity.ok("구매 요청을 수락하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 쇼핑몰 주인의 상품 구매 요청 거절 (정당한 사유 기입)
    @PostMapping("/purchase-reject/{itemOrderId}")
    public ResponseEntity<String> rejectItem(
            @PathVariable
            Long itemOrderId,
            @RequestBody
            PaymentCancelDto dto
    ) {
        try {
            service.rejectItem(itemOrderId, dto);
            return ResponseEntity.ok("구매 요청을 거절하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
