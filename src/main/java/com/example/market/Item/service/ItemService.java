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
            String username = "Admin";
            Item item = Item.builder()
                    .title("해피해킹 스튜디오")
                    .content("구성품: 키보드 본체, 루프, 포인트 키캡")
                    .price(430000)
                    .status("판매중")
                    .writer(username)
                    .build();
            this.itemRepository.save(item);
        } else {
            log.warn("인증된 사용자 정보가 없습니다.");
        }
    }

    // 물품 전체 조회 서비스
    public List<ItemDto> readAll() {
        return itemRepository.findAll().stream()
                .map(item -> {
                    ItemDto itemDto = ItemDto.fromEntity(item);
                    itemDto.setWriter(item.getWriter()); // 작성자의 username을 설정
                    return itemDto;
                })
                .toList();
    }


}
