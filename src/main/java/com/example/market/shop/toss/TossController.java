package com.example.market.shop.toss;

// 프론트엔드에서 보낸 승인 요청을 받아주는 컨트롤러

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/toss")
@RequiredArgsConstructor
public class TossController {
    private final TossOrderService service;
    @PostMapping("/confirm-payment")
    // 모양새가 복잡하기 떄문에 임시로 Object 로 응답을 만들어줌
    public Object confirmPayment(
            @RequestBody
            TossPaymentConfirmDto dto
    ) {
        log.info("received: {}", dto.toString());
        return service.confirmPayment(dto);
    }

}
