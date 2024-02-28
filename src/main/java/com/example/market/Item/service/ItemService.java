package com.example.market.Item.service;

import com.example.market.Item.dto.ItemDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
// @RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;

        if (this.itemRepository.count() == 0) {
            this.itemRepository.saveAll(List.of(
                    Item.builder()
                            .title("해피해킹 스튜디오")
                            .content("구성품 : 키보드 본체, 루프, 포인트 키캡")
                            .price(430000)
                            .status("판매중")
                            .build()
            ));
        }
    }

    public List<ItemDto> readAll() {
        return itemRepository.findAll().stream()
                .map(ItemDto::fromEntity)
                .toList();
    }

    public ItemDto readOne(Long id) {
        return itemRepository.findById(id)
                .map(ItemDto::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


}