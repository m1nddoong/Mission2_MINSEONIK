package com.example.market.shop.dto;


import com.example.market.shop.entity.Shop;
import com.example.market.shop.entity.ShopCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {
    private Long id;
    private String shopName;
    private String introduction;
    private String category;
    private Long ownerId;
    private String shopStatus;
}

// 쇼핑몰의 이름, 소개, 분류가 전부 작성된 상태라면 쇼핑몰을 개설 신청을 할 수 있다.
