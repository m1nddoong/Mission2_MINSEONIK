package com.example.market.shop.dto;


import com.example.market.shop.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String content;
    private Integer price;
    private String category;
    private String subCategory;
    private int stock;
    private Long writerId;

    public static ItemDto fromEntity(Item item) {
      /* 빌더 패턴 사용
        return ProductDto.builder()
        .id(product.getId())
        .name(product.getName())
        .imageUrl(product.getImageUrl())
        .content(product.getContent())
        .price(product.getPrice())
        .category(product.getCategory())
        .subCategory(product.getSubCategory())
        .stock(product.getStock())
        .writerId(product.getWriter().getId())
        .build(); */

        // 일반
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getImageUrl(),
                item.getContent(),
                item.getPrice(),
                item.getCategory(),
                item.getSubCategory(),
                item.getStock(),
                item.getWriter().getId()
        );
    }

}
