package com.example.market.shop.controller;

import com.example.market.shop.dto.EmailDto;
import com.example.market.shop.dto.ShopDto;
import com.example.market.shop.service.ShopService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService service;

    /**
     * 쇼핑몰 정보 수정
     * @param dto
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateShop(
            @RequestBody
            ShopDto dto
    ) {
        service.updateShop(dto);
        return ResponseEntity.ok("쇼핑몰 정보를 수정했습니다.");
    }

    /**
     * 쇼핑몰의 이름, 소개, 분류가 전부 작성된 상태라면 쇼핑몰을 개설 신청을 할 수 있다.
     * @return
     */
    @GetMapping("/apply-opening")
    public ResponseEntity<String> applyOpening() {
        if (service.applyOpening()) {
            return ResponseEntity.ok("쇼핑몰 개설이 신청 되었습니다!");
        } else {
            return ResponseEntity.badRequest().body("쇼핑몰이 개설 신청이 되지 않았습니다.");
        }

    }

    /**
     * 관리자의 개설인 신청된 쇼핑몰 목록 확인
     * @return
     */
    @GetMapping("/get-all-open-requests")
    public ResponseEntity<List<ShopDto>> getAllOpenRequests() {
        List<ShopDto> openRequests = service.getAllOpenRequests();
        return ResponseEntity.ok(openRequests);
    }


    /**
     * 쇼핑몰 개설 신청 허가
     * @param shopId 쇼핑몰의 id
     * @return 개설이 허가된 쇼핑몰은 오픈 상태가 된다.
     */
    @PostMapping("/approve-shop/{shopId}")
    public ResponseEntity<String> approveShop(
            @PathVariable
            Long shopId
    ) {
        service.approveShop(shopId);
        return ResponseEntity.ok("쇼핑몰 허가 완료");
    }


    // 관지라는 쇼핑몰의 id를 전달받아서, 해당 쇼핑몰을 불허한다.
    // 이떄 불허된 이유를 함께 작성하고
    // 이를 쇼핑몰의 주인, 즉 해당 쇼핑몰의 객체의 owner 에게 전달해야 한다.
    @PostMapping("/reject-shop/{shopId}")
    public ResponseEntity<String> rejectShop(
            @PathVariable
            Long shopId,
            @RequestBody
            EmailDto dto
    ) {
        service.rejectShop(shopId, dto);
        return ResponseEntity.ok("쇼핑몰 불허 완료");
    }





    // 쇼핑몰의 주인은 사유를 작성하여 쇼핑몰 폐쇄 요청을 할 수 있다.


    // 관리자는 쇼핑몰 폐쇄 요청을 확인 후 수락할 수 있다.


}
