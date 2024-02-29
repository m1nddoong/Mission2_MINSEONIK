package com.example.market.Item.controller;


import com.example.market.Item.dto.ItemProposalDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.entity.ItemProposal;
import com.example.market.Item.entity.ProposalStatus;
import com.example.market.Item.repo.ItemProposalRepository;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.Item.service.ItemProposalService;
import com.example.market.entity.CustomUserDetails;
import com.example.market.repo.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemProposalController {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemProposalRepository itemProposalRepository;
    private final ItemProposalService service;

    /**
     * 구매 제안 확인
     * 등록된 구매 제안은 물품을 등록한 사용자와 제안을 등록한 사용자만 조회 가능
     * @param itemId
     * @return
     */
    @GetMapping("/{itemId}/proposals")
    public ResponseEntity<List<ItemProposalDto>> getItemProposals(
        @PathVariable
        Long itemId
    ) {
        // 현재 인증된 사용자 정보 가져오기
        List<ItemProposalDto> proposals = service.getItemProposals(itemId);
        return ResponseEntity.ok(proposals);
    }

    /**
     * 상품 구매 제안 등록하기
     * @param itemId
     * @return
     */
    @PostMapping("/{itemId}/proposals")
    public ResponseEntity<String> createProposal(
            @PathVariable
            Long itemId
    ) {
        service.createProposal(itemId);
        return ResponseEntity.ok("상품 구매 제안이 등록되었습니다.");
    }
}