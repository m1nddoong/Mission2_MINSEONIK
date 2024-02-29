package com.example.market.Item.service;


import com.example.market.Item.dto.ItemProposalDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.entity.ItemProposal;
import com.example.market.Item.entity.ProposalStatus;
import com.example.market.Item.repo.ItemProposalRepository;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.entity.CustomUserDetails;
import com.example.market.entity.UserEntity;
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


    /**
     * 구매 제안 수락
     * @param proposalId
     */
    public void acceptProposal(Long itemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
        // 현재 인증된 사용자의 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 물품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 itemId의 상품을 찾을 수 없습니다.");
        Item item = optionalItem.get(); // GMK67

        // 해당 물품을 등록한 사용자라면
        if (item.getWriter().equals(username)) {
            log.info("당신은 물품을 등록한 {} 입니다. 제안을 수락하겠습니다.", item.getWriter());
            // 구매 제안 정보
            Optional<ItemProposal> optionalProposal = itemProposalRepository.findById(proposalId);
            if (optionalProposal.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 구매 제안을 찾을 수 없습니다.");
            ItemProposal proposal = optionalProposal.get();
            // 구매 제인을 수락상태로 변경
            proposal.setStatus(ProposalStatus.ACCEPTED);
            itemProposalRepository.save(proposal);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 수락할 수 있습니다.");
        }
    }


    /**
     * 구매 제안 거절
     * @param proposalId
     */
    public void rejectProposal(Long itemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
        // 현재 인증된 사용자의 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 물품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 itemId의 상품을 찾을 수 없습니다.");
        Item item = optionalItem.get(); // GMK67

        // 해당 물품을 등록한 사용자라면
        if (item.getWriter().equals(username)) {
            log.info("당신은 물품을 등록한 {} 입니다. 제안을 거절하겠습니다.", item.getWriter());
            // 구매 제안 정보
            Optional<ItemProposal> optionalProposal = itemProposalRepository.findById(proposalId);
            if (optionalProposal.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 구매 제안을 찾을 수 없습니다.");
            ItemProposal proposal = optionalProposal.get();
            // 구매 제인을 수락상태로 변경
            proposal.setStatus(ProposalStatus.REJECTED);
            itemProposalRepository.save(proposal);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 거절할 수 있습니다.");
        }

    }

    // 제안을 등록한 사용자의 구매 확정
    // 좀 복잡하다.
    public void confirmProposal(Long itemId, Long proposalId) {
        // 현재 인증된 사용자의 username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // username 을 통해 사용자 조회 (userEntity 를 받는다)
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get();

        // 구매 제안 정보 조회
        Optional<ItemProposal> optionalProposal = itemProposalRepository.findById(proposalId);
        if (optionalProposal.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 구매 제안을 찾을 수 없습니다.");
        ItemProposal proposal = optionalProposal.get();

        // 물품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 itemId의 상품을 찾을 수 없습니다.");
        Item item = optionalItem.get();

        // 해당 물품을 구매 제안한 사용자라면?
        // 구매 제안 정보중 proposer의 id 가 userEntity 의 id 와 같다면
        if (proposal.getProposer().getId().equals(userEntity.getId())) {
            // 구매 제안 상태가 "ACCEPTED" 일 경우 아래 진행 (따로 조건문은 작성하지 않음)
            // 1. 구매 제안 상태를 "확정 상태" 로 변경
            proposal.setStatus(ProposalStatus.CONFIRMED);
            itemProposalRepository.save(proposal);

            // 2. 구매 제안이 "확정 상태"가 될 경우, 물품의 상태는 판매 완료가 된다.
            item.setStatus("판매완료");
            itemRepository.save(item);

            // 3. 구매 제안이 "확정 상태"가 될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 거절된다.
            List<ItemProposal> otherProposals = itemProposalRepository.findByItemAndIdNot(item, proposalId);
            for (ItemProposal otherProposal : otherProposals) {
                otherProposal.setStatus(ProposalStatus.REJECTED);
                itemProposalRepository.save(otherProposal);
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매를 확정할 수 있습니다.");
        }

    }
}
