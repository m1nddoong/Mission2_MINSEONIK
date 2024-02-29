package com.example.market.Item.service;


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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemProposalService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemProposalRepository itemProposalRepository;


    /**
     * 상품 구매 제안 등록하기
     * @param itemId
     * @return
     */
    public void createProposal(Long itemId) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByUsername(userDetails.getUsername()).get().getId();

        // 물품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Item item = optionalItem.get();

        // 해당 물품을 등록한 사용자는 제외
        if (item.getWriter().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사람은 구매 제안을 등록할 수 없습니다.");
        }

        // 구매 제안 등록
        ItemProposal proposal = new ItemProposal();
        proposal.setItem(item);
        proposal.setProposer(userRepository.getReferenceById(userId)); // UserEntity 객체 설정
        proposal.setStatus(ProposalStatus.PENDING); // 대기 중 상태로 등록
        itemProposalRepository.save(proposal);
    }



    /**
     * 구매 제안 확인
     * 등록된 구매 제안은 물품을 등록한 사용자와 제안을 등록한 사용자만 조회 가능
     * @param itemId
     * @return
     */
    public List<ItemProposalDto> getItemProposals(Long itemId) {
        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // USER1
        // 물품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Item item = optionalItem.get(); // GMK67

        // 해당 물품을 등록한 사용자라면
        if (item.getWriter().equals(username)) {
            log.info("당신은 물품을 등록한 {} 입니다.", item.getWriter());
            // 해당 물품에 대한 모든 제안을 확인 가능
            List<ItemProposal> allProposals = itemProposalRepository.findByItem(item);
            return allProposals.stream()
                    .map(ItemProposalDto::fromEntity)
                    .collect(Collectors.toList());
        }
        else {
            // 해당 물품의 구매를 제안한 사용자라면
            log.info("당신은 해당 물품에 대한 구매를 제안한 {} 입니다.", username);
            List<ItemProposal> userProposals = itemProposalRepository.findByItemAndProposerUsername(item, username);
            return userProposals.stream()
                    .map(ItemProposalDto::fromEntity)
                    .collect(Collectors.toList());
        }
    }


}
