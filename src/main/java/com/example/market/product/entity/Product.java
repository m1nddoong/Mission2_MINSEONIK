package com.example.market.product.entity;



// 쇼핑몰 상품

import com.example.market.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String name; // 상품 이름
    @Setter
    private String imageUrl; // 상품 이미지
    @Setter
    private String content; // 상품 설명
    @Setter
    private Integer price; // 상품 가격
    @Setter
    private String category; // 상품 분류
    @Setter
    private String subCategory; // 상품 소분류
    @Setter
    private int Stock; // 상품 재고

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity writer; // 상품 등록자

}
