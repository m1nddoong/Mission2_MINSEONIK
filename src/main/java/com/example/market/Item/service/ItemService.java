package com.example.market.Item.service;

import com.example.market.Item.dto.ItemDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
// @RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

        if (this.itemRepository.count() == 0) {
            String username = "Admin";
            Item item1 = Item.builder()
                    .title("해피해킹 하이브리드 type-s")
                    .content("토프레 무접점, 키보드 루프, 파우치, 전용 팜레스트")
                    .price(300000)
                    .status("판매중")
                    .writer(username)
                    .build();

            username = "BusinessUser";
            Item item2 = Item.builder()
                    .title("GMK67 커스텀 키보드")
                    .content("핫스왑 기능, 67배열, 하이무 미드나잇 스위치")
                    .price(35000)
                    .status("판매중")
                    .writer(username)
                    .build();

            username = "User1";
            Item item3 = Item.builder()
                    .title("한성 GK868b 키보드")
                    .content("노뿌 무접점, 풀윤활 O")
                    .price(120000)
                    .status("판매중")
                    .writer(username)
                    .build();

            this.itemRepository.save(item1);
            this.itemRepository.save(item2);
            this.itemRepository.save(item3);

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


    // 물품을 수정하는 서비스
    @Transactional
    public boolean updateItem(ItemDto dto, Long itemId, String username) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item itemEntity = optionalItem.get();
            if (itemEntity.getWriter().equals(username)) {
                itemEntity.setTitle(dto.getTitle());
                itemEntity.setContent(dto.getContent());
                itemEntity.setPrice(dto.getPrice());
                itemEntity.setStatus(dto.getStatus());
                itemRepository.save(itemEntity);
                return true;
            }
        }
        return false; // 물품을 찾을 수 없거나 권한이 없음
    }

    @Transactional
    public boolean deleteItem(Long itemId, String username) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item existingItem = optionalItem.get();
            if (existingItem.getWriter().equals(username)) {
                itemRepository.delete(existingItem);
                return true;
            }
        }
        return false; // 물품을 찾을 수 없거나 권한이 없음
    }
}
