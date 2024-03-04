package com.example.market.shop.dto;

import com.example.market.shop.entity.ItemOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrderDto {
    private Long id;
    private Long itemId;

    public static ItemOrderDto fromEntity(ItemOrder itemOrder) {
        return new ItemOrderDto(
                itemOrder.getId(),
                itemOrder.getItem().getId()
        );
    }

}
