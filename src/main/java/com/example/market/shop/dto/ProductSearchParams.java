package com.example.market.shop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductSearchParams {
    // 검색 기준을 담기 위한 DTO의 일종
    private String name;
    private Integer priceFloor;
    private Integer priceCeil;
}
