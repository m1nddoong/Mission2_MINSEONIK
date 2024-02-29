package com.example.market.shop.entity;

import com.example.market.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String shopName; // 이름

    @Setter
    private String introduction; // 소개

    @Setter
    private ShopCategory category; // 분류

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity owner; // 주인은 자유롭게 수정이 가능

    @Setter
    @Enumerated(EnumType.STRING)
    private ShopStatus status;

    // 생성자, 게터, 세터, 기타 메서드는 생략되었습니다.
}