package com.example.market.shop.controller;


import com.example.market.shop.dto.ItemDto;
import com.example.market.shop.dto.ProductSearchParams;
import com.example.market.shop.repo.ItemRepository;
import com.example.market.shop.service.ItemService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private final ItemRepository productRepository;

    /**
     * 쇼핑몰에 상품 등록
     * @param dto 상품 등록 정보
     * @return 등록 완료 (쇼핑몰 주인만 가능)
     */
    @PostMapping("/create")
    public ResponseEntity<String> createItem(
            @RequestBody
            ItemDto dto
    ) {
        if (service.createItem(dto)) {
            return ResponseEntity.ok("쇼핑몰에 상품을 등록하였습니다.");
        } else {
            return ResponseEntity.ok("쇼핑몰이 개설되지 않았거나, 상품 등록에 실패하였습니다.");
        }
    }

    /**
     *  TODO : 상품 이미지 추가는 제외 (나중에 구현)
     */


    /**
     * 쇼핑몰 상품 수정
     * @param dto
     * @return
     */
    @PutMapping("{itemId}/update")
    public ResponseEntity<String> updateItem(
            @PathVariable
            Long itemId,
            @RequestBody
            ItemDto dto
    ) {
        if (service.updateItem(itemId, dto)) {
            return ResponseEntity.ok("쇼핑몰의 상품이 수정되었습니다.");
        } else {
            return ResponseEntity.ok("쇼핑몰이 개설되지 않았거나, 상품 수정에 실패하였습니다.");
        }
    }


    /**
     * 쇼핑몰 상품 삭제
     * @param itemId
     * @return
     */
    @DeleteMapping("{itemId}/delete")
    public ResponseEntity<String> deleteItem(
            @PathVariable
            Long itemId
    ) {
        if (service.deleteItem(itemId)) {
            return ResponseEntity.ok("쇼핑몰의 상품이 삭제되었습니다.");
        } else {
            return ResponseEntity.ok("쇼핑몰이 개설되지 않았거나, 상품 삭제에 실패하였습니다.");
        }
    }


    @GetMapping("search")
    public List<ItemDto> search(
            // Query Parameter를 받아온다.
            // /serach?name=name&priceFloor=1&priceCeil=10
            // 이렇게 파라미터를 넣어줄 수 있는데, 파라미터 생략이 되면 null 이 들어
            ProductSearchParams searchParams
    ) {
        return productRepository.searchDynamic(searchParams)
                .stream()
                .map(ItemDto::fromEntity)
                .collect(Collectors.toList());
    }

}
