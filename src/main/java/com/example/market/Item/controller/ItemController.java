package com.example.market.Item.controller;

import com.example.market.Item.dto.ItemDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.Item.service.ItemService;
import java.awt.event.ItemEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemRepository itemRepository;


    @GetMapping
    public List<ItemDto> readAll() {
        return itemService.readAll();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerItem(
            @RequestBody
            ItemDto dto
    ) {
        Item itemEntity = Item.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .price(dto.getPrice())
                .status("판매중") // 기본값 설정
                .build();

        itemRepository.save(itemEntity);

        return ResponseEntity.ok("물품이 성공적으로 등록되었습니다.");
    }


}
