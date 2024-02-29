package com.example.market.Item.controller;


import com.example.market.Item.dto.ItemProposalDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.entity.ItemProposal;
import com.example.market.Item.entity.ProposalStatus;
import com.example.market.Item.repo.ItemProposalRepository;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.entity.CustomUserDetails;
import com.example.market.repo.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // USER1

        // 물품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get(); // GMK67

        log.info("현재 인증된 사용자 : {}", username);
        log.info("물품의 작성자 이름 : {}", item.getWriter());

        // 해당 물품을 등록한 사용자라면
        if (item.getWriter().equals(username)) {
            log.info("해당 물품을 등록한 사용자입니다.");
            // 해당 물품에 대한 모든 제안을 확인 가능
            List<ItemProposal> allProposals = itemProposalRepository.findByItem(item);
            List<ItemProposalDto> proposalDtos = allProposals.stream()
                    .map(ItemProposalDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(proposalDtos);
        }

        // 해당 물품의 구매를 제안한 사용자라면
        // 해당 물품에 대해 내의 제안만 확인 가능
        log.info("해당 물품에 대한 구매를 제안한 사용자입니다.");
        List<ItemProposal> userProposals = itemProposalRepository.findByItemAndProposerUsername(item, username);
        List<ItemProposalDto> proposalDtos = userProposals.stream()
                .map(ItemProposalDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(proposalDtos);

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