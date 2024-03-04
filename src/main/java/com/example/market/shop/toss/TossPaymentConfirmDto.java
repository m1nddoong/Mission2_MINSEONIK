package com.example.market.shop.toss;

// 결제를 승인하기 위한 dto
// 프론트엔드에서 보내는 것 3가지 (paymentId, orderId, amount) 를 포함하기 위한 dto

import lombok.Data;

@Data
public class TossPaymentConfirmDto {
    // 클라이언트로부터 이런 데이터가 들어올 것
    private String paymentKey;
    private String orderId;
    private Integer amount;
}
