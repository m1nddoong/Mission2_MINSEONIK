package com.example.market.usedItem.controller;


import com.example.market.usedItem.dto.UsedItemProposalDto;
import com.example.market.usedItem.repo.UsedItemProposalRepository;
import com.example.market.usedItem.repo.UsedItemRepository;
import com.example.market.usedItem.service.UsedItemProposalService;
import com.example.market.user.repo.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("used-items")
@RequiredArgsConstructor
public class UsedItemProposalController {
    private final UsedItemRepository usedItemRepository;
    private final UserRepository userRepository;
    private final UsedItemProposalRepository usedItemProposalRepository;
    private final UsedItemProposalService service;

    /**
     * 중고 물품 구매 제안 등록하기
     * @param usedItemId
     * @return
     */
    @PostMapping("/{usedItemId}/proposals")
    public ResponseEntity<String> createProposal(
            @PathVariable
            Long usedItemId
    ) {
        service.createProposal(usedItemId);
        return ResponseEntity.ok("중고 물품 구매 제안이 등록되었습니다.");
    }

    /**
     * 중고 물품 구매 제안 확인
     * 등록된 구매 제안은 물품을 등록한 사용자와 제안을 등록한 사용자만 조회 가능
     * @param usedItemId
     * @return
     */
    @GetMapping("/{usedItemId}/proposals")
    public ResponseEntity<List<UsedItemProposalDto>> getItemProposals(
            @PathVariable
            Long usedItemId
    ) {
        // 현재 인증된 사용자 정보 가져오기
        List<UsedItemProposalDto> proposals = service.getItemProposals(usedItemId);
        return ResponseEntity.ok(proposals);
    }


    /**
     * 중고 물품을 등록한 사용자의 구매 제안 수락
     * @param usedItemId
     * @param proposalId
     * @return
     */
    @PostMapping("/{usedItemId}/proposals/{proposalId}/accept")
    public ResponseEntity<String> acceptProposal(
            @PathVariable("usedItemId")
            Long usedItemId,
            @PathVariable("proposalId")
            Long proposalId
    ) {
        service.acceptProposal(usedItemId, proposalId);
        return ResponseEntity.ok("중고 물품의 구매 제안이 성공적으로 수락되었습니다.");

    }

    /**
     * 중고 물품을 등록한 사용자의 구매 제안 거절
     * @param usedItemId
     * @param proposalId
     * @return
     */
    @PostMapping("/{usedItemId}/proposals/{proposalId}/reject")
    public ResponseEntity<String> rejectProposal(
            @PathVariable("usedItemId")
            Long usedItemId,
            @PathVariable("proposalId")
            Long proposalId
    ) {
        service.rejectProposal(usedItemId, proposalId);
        return ResponseEntity.ok("중고 물품의 구매 제안이 성공적으로 거절되었습니다.");

    }

    /**
     * 제안을 등록한 사용자의 구매 확정 (수락 상태 일 경우)
     * @param usedItemId
     * @param proposalId
     * @return
     */
    @PostMapping("/{usedItemId}/proposals/{proposalId}/confirm")
    public ResponseEntity<String> confirmProposal(
            @PathVariable("usedItemId")
            Long usedItemId,
            @PathVariable("proposalId")
            Long proposalId
    ) {
        service.confirmProposal(usedItemId, proposalId);
        return ResponseEntity.ok("중고 물품의 구매 제안이 확정되었습니다.");
    }
}