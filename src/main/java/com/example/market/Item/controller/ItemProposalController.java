package com.example.market.Item.controller;


import com.example.market.Item.entity.Item;
import com.example.market.Item.entity.ItemProposal;
import com.example.market.Item.entity.ProposalStatus;
import com.example.market.Item.repo.ItemProposalRepository;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.Item.service.ItemService;
import com.example.market.entity.CustomUserDetails;
import com.example.market.repo.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/{itemId}/proposals")
    public ResponseEntity<String> createProposal(
            @PathVariable
            Long itemId
    ) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByUsername(userDetails.getUsername()).get().getId();


        // 물품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();

        // 해당 물품을 등록한 사용자는 제외
        if (item.getWriter().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 물품을 등록한 사람은 구매 제안을 등록할 수 없습니다.");
        }

        // 구매 제안 등록
        ItemProposal proposal = new ItemProposal();
        proposal.setItem(item);
        proposal.setProposer(userRepository.getReferenceById(userId)); // UserEntity 객체 설정
        proposal.setStatus(ProposalStatus.PENDING); // 대기 중 상태로 등록
        itemProposalRepository.save(proposal);

        return ResponseEntity.ok("상품 구매 제안이 등록되었습니다.");
    }


}