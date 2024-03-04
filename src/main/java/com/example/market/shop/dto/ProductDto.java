package com.example.market.shop.dto;


import com.example.market.shop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String content;
    private Integer price;
    private String category;
    private String subCategory;
    private int stock;
    private Long writerId;

    public static ProductDto fromEntity(Product product) {
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
        return new ProductDto (
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getContent(),
                product.getPrice(),
                product.getCategory(),
                product.getSubCategory(),
                product.getStock(),
                product.getWriter().getId()
        );
    }

}
