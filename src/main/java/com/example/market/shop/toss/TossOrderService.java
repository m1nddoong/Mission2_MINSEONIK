package com.example.market.shop.toss;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossOrderService {
    private final TossHttpService tossService;

    // 내가 돈을 받겠다 : 결제 승인
    public Object confirmPayment(TossPaymentConfirmDto dto){
        // HTTP 요청이 보내진다.
        Object tossPaymentObj = tossService.confirmPayment(dto);
        log.info(tossPaymentObj.toString());
        return tossPaymentObj;
    }





    //-------------------------------------------------------------



}