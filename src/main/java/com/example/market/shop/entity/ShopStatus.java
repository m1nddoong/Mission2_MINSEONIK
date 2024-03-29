package com.example.market.shop.entity;

public enum ShopStatus {
    PREPARING, // 준비중
    OPEN,      // 오픈
    CLOSED,    // 폐쇄
    OPEN_REQUESTED, // 오픈 요청
    CLOSED_REQUESTED, // 폐쇄 요청
    OPEN_REQUEST_REJECTED    // 오픈 요청 거절

    // OPEN_REQUEST_APPROVED,  // 오픈 요청 허가
}
