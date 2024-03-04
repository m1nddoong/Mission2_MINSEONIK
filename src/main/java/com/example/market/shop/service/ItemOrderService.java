package com.example.market.shop.service;

import com.example.market.shop.dto.ItemOrderDto;
import com.example.market.shop.dto.PaymentCancelDto;
import com.example.market.shop.entity.Item;
import com.example.market.shop.entity.ItemOrder;
import com.example.market.shop.entity.Shop;
import com.example.market.shop.repo.ItemOrderRepository;
import com.example.market.shop.repo.ItemRepository;
import com.example.market.shop.repo.ShopRepository;
import com.example.market.user.entity.UserEntity;
import com.example.market.user.repo.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
// @RequiredArgsConstructor
public class ItemOrderService {
    private final ItemOrderRepository itemOrderRepository;
    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ItemOrderService(
            ItemRepository itemRepository,
            ShopRepository shopRepository,
            UserRepository userRepository,
            ItemOrderRepository itemOrderRepository
    ) {
        this.itemRepository = itemRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.itemOrderRepository = itemOrderRepository;
    }


    // 상품 구매 요청
    public void purchaseItem(ItemOrderDto dto) {
        UserEntity userEntity = getCurrentUser();
        Item item = getItemFromId(dto.getItemId());

        // 결제 진행, 자동 재고 갱신
        if (item.getStock() > 0) {
            // 주문 정보 생성 및 저장
            ItemOrder order = new ItemOrder();
            order.setItem(item);
            order.setConsumer(userEntity);
            order.setStatus("PENDING");
            itemOrderRepository.save(order);

            // 상품 재고 수정
            item.setStock(item.getStock() - 1);
            itemRepository.save(item);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "재고가 부족합니다.");
        }

    }

    // 쇼핑몰 주인의 구매 요청 수락
    public void approveItem(Long itemOrderId) {
        // 주문 id 를 통해서 자기 자신이 이 쇼핑몰이 주인이 맞는지.
        UserEntity userEntity = getCurrentUser();
        ItemOrder order = getItemOrderFromId(itemOrderId);

        // 주문정보에 포함된 상품의 주인의 id 와 현재 인증된 사용자의 id 가 같다면 => 주인
        if (order.getItem().getWriter().getId().equals(userEntity.getId())) {
            order.setStatus("ACCEPTED"); // 이후엔 구매 취소가 불가능하다.
            itemOrderRepository.save(order);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "권한이 없습니다.");
        }

    }


    // 쇼핑몰 주인의 구매 요청 거절
    public void rejectItem(Long itemOrderId, PaymentCancelDto dto) {
        // 주문 id 를 통해서 자기 자신이 이 쇼핑몰이 주인이 맞는지.
        UserEntity userEntity = getCurrentUser();
        ItemOrder order = getItemOrderFromId(itemOrderId);

        // 주문정보에 포함된 상품의 주인의 id 와 현재 인증된 사용자의 id 가 같다면 => 주인
        if (order.getItem().getWriter().getId().equals(userEntity.getId())) {
            order.setStatus("REJECTED_REQUEST");
            itemOrderRepository.save(order);

            // 관리자가 확인할 수 있게 거절 사유를 제출
            log.info("{} 님의 구매 요청 거절 사유 : {}", userEntity.getUsername(), dto.getCancelReason());
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "권한이 없습니다.");
        }

    }

    // 구매자의 상품 구매 취소
    public void cancelItemPayment(Long itemOrderId) {
        // 주문 id 를 통해서 자기 자신이 이 쇼핑몰이 주인이 맞는지.
        UserEntity userEntity = getCurrentUser();
        ItemOrder order = getItemOrderFromId(itemOrderId);
        Item item = getItemFromId(order.getItem().getId());

        // 주문정보에 포함된 상품의 구매자의 id 와 현재 인증된 사용자의 id 가 같다면 => 주인
        if (order.getConsumer().getId().equals(userEntity.getId())) {
            // 구매 요청 상태가 "수락" 이 아닐 경우
            if (!order.getStatus().equals("ACCEPTED")) {
                order.setStatus("PURCHASE_CANCEL"); // 구메 요청 취소
                itemOrderRepository.save(order);
                item.setStock(item.getStock()+1);
                itemRepository.save(item);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "권한이 없거나, 구매 요청이 수락 상태 입니다.");
        }
    }


    /*
   <===================================================================>
   사용자 객체, 쇼핑몰 객체, 쇼핑몰 상품 객체를 가져오는 부분을 따로 메서드로 추출
   <===================================================================>
    */

    // 사용자의 인증 정보를 가져올 메서드
    private UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }
        return optionalUserEntity.get();
    }

    private Item getItemFromId(Long itemId) {
        Optional<Item> optionalitem = itemRepository.findById(itemId);
        if (optionalitem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 정보를 찾을 수 없습니다.");
        }
        return optionalitem.get();
    }

    private ItemOrder getItemOrderFromId(Long itemOrderId) {
        Optional<ItemOrder> optionalItemOrder = itemOrderRepository.findById(itemOrderId);
        if (optionalItemOrder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 주문 정보를 찾을 수 없습니다.");
        }
        return optionalItemOrder.get();
    }


}
