package com.example.market.usedItem.controller;

import com.example.market.usedItem.dto.UsedItemDto;
import com.example.market.usedItem.service.UsedItemService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("used-items")
@RequiredArgsConstructor
public class UsedItemController {
    private final UsedItemService service;

    /**
     * 일반 사용자의 중고 물품 등록
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerItem(
            @RequestBody
            UsedItemDto dto
    ) {
        service.registerItem(dto);
        return ResponseEntity.ok("물품이 성공적으로 등록되었습니다.");
    }

    /**
     * 일반 사용자가 등록한 중고 물품의 이미지 등록
     * @param multipartFile
     * @param itemId
     * @return
     * @throws IOException
     */
    @PostMapping(
            value = "/register-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> registerItemImage(
            @RequestParam("file")
            MultipartFile multipartFile,
            @RequestParam("usedItemId")
            Long itemId
    ) throws IOException {
        service.registerItemImage(multipartFile, itemId);
        return ResponseEntity.ok("중고 물품 이미지가 성공적으로 등록되었습니다.");
    }



    /**
     * 중고 물품 전체 조회
     * @return
     */
    @GetMapping
    public List<UsedItemDto> readAll() {
        return service.readAll();
    }




    /**
     * 중고 물품 수정
     * @param dto
     * @return
     */
    @PutMapping("/update/{usedItemId}")
    public ResponseEntity<String> updateUsedItem(
            @PathVariable
            Long usedItemId,
            @RequestBody
            UsedItemDto dto
    ) {
        if (service.updateItem(dto, usedItemId)) {
            return ResponseEntity.ok("중고 물품이 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("중고 물품을 찾을 수 없거나 권한이 없습니다.");
        }
    }

    /**
     * 중고 물품 삭제
     * @return
     */
    @DeleteMapping("/delete/{usedItemId}")
    public ResponseEntity<String> deleteUsedItem(
            @PathVariable
            Long usedItemId
    ) {
        if (service.deleteItem(usedItemId)) {
            return ResponseEntity.ok("중고 물품이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("중고 물품을 찾을 수 없습니다.");
        }
    }


}
