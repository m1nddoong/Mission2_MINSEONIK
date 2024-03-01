package com.example.market.product.dto;


import com.example.market.product.entity.Product;
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
                .build();
    }

}
