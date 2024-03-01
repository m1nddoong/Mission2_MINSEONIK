package com.example.market.shop.dto;


import com.example.market.shop.entity.Shop;
import java.time.LocalDateTime;
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
    private LocalDateTime recentTransactionDate;


    public static ShopDto fromEntity(Shop shop) {
        return ShopDto.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .introduction(shop.getIntroduction())
                .category(shop.getCategory().name()) // Enum을 문자열로 반환
                .ownerId(shop.getOwner().getId())
                .shopStatus(shop.getShopStatus().name()) //Enum을 문자열로 반환
                .recentTransactionDate(shop.getRecentTransactionDate())
                .build();
    }


}

// 쇼핑몰의 이름, 소개, 분류가 전부 작성된 상태라면 쇼핑몰을 개설 신청을 할 수 있다.
