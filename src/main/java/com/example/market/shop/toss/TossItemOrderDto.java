package com.example.market.shop.toss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossItemOrderDto {
    private Long id;
    private Long itemId;
    private String itemName;
    private String tossPaymentKey;
    private String tossOrderId;
    private String status;

    public static TossItemOrderDto fromEntity(TossItemOrder entity) {
        return TossItemOrderDto.builder()
                .id(entity.getId())
                .itemId(entity.getItem().getId())
                .itemName(entity.getItem().getName())
                .tossPaymentKey(entity.getTossPaymentKey())
                .tossOrderId(entity.getTossOrderId())
                .status(entity.getStatus())
                .build();
    }
}