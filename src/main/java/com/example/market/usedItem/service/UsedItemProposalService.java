package com.example.market.usedItem.service;


import com.example.market.usedItem.entity.UsedItem;
import com.example.market.usedItem.entity.UsedItemProposal;
import com.example.market.usedItem.entity.ProposalStatus;
import com.example.market.usedItem.repo.UsedItemProposalRepository;
import com.example.market.usedItem.repo.UsedItemRepository;
import com.example.market.usedItem.dto.UsedItemProposalDto;
import com.example.market.user.entity.CustomUserDetails;
import com.example.market.user.entity.UserEntity;
import com.example.market.user.repo.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsedItemProposalService {
    private final UsedItemRepository usedItemRepository;
    private final UserRepository userRepository;
    private final UsedItemProposalRepository usedItemProposalRepository;

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
        Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        UsedItem usedItem = optionalItem.get();

        // 해당 물품을 등록한 사용자는 제외
        if (usedItem.getWriter().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사람은 구매 제안을 등록할 수 없습니다.");
        }

        // 구매 제안 등록
        UsedItemProposal proposal = new UsedItemProposal();
        proposal.setUsedItem(usedItem);
        proposal.setProposer(userRepository.getReferenceById(userId)); // UserEntity 객체 설정
        proposal.setStatus(ProposalStatus.PENDING); // 대기 중 상태로 등록
        usedItemProposalRepository.save(proposal);
    }


    /**
     * 구매 제안 확인
     * 등록된 구매 제안은 물품을 등록한 사용자와 제안을 등록한 사용자만 조회 가능
     * @param itemId
     * @return
     */
    public List<UsedItemProposalDto> getItemProposals(Long itemId) {
        // 물품 조회
        Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "물품 정보를 찾을 수 없습니다.");
        UsedItem usedItem = optionalItem.get(); // 해피해킹

        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get(); // USER1


        // 해당 물품을 등록한 사용자라면
        if (usedItem.getWriter().getId().equals(userEntity.getId())) {
            log.info("딩신은 이 물품을 등록한 {}입니다.", usedItem.getWriter().getUsername());
            // 해당 물품에 대한 다른 사람들의 모든 구매 제안을 확인 가능
            List<UsedItemProposal> allProposals = usedItemProposalRepository.findByUsedItem(usedItem);
            return allProposals.stream()
                    .map(UsedItemProposalDto::fromEntity)
                    .collect(Collectors.toList());
        }
        else {
            // 해당 물품의 구매를 제안한 사용자라면
            log.info("당신은 해당 물품에 대한 구매를 제안한 {} 입니다.", username);
            List<UsedItemProposal> userProposals = usedItemProposalRepository.findByUsedItemAndProposerUsername(usedItem, username);
            return userProposals.stream()
                    .map(UsedItemProposalDto::fromEntity)
                    .collect(Collectors.toList());
        }
    }


    /**
     * 구매 제안 수락
     * @param proposalId
     */
    public void acceptProposal(Long itemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
        // 물품 조회
        Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "물품 정보를 찾을 수 없습니다.");
        UsedItem usedItem = optionalItem.get(); // 해피해킹

        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get(); // USER1

        // 해당 물품을 등록한 사용자라면
        if (usedItem.getWriter().getId().equals(userEntity.getId())) {
            log.info("당신은 이 물품을 등록한 {} 입니다. (제안 수락)", usedItem.getWriter().getUsername());
            // 구매 제안 정보
            Optional<UsedItemProposal> optionalProposal = usedItemProposalRepository.findById(proposalId);
            if (optionalProposal.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 구매 제안을 찾을 수 없습니다.");
            UsedItemProposal proposal = optionalProposal.get();
            // 구매 제인을 수락상태로 변경
            proposal.setStatus(ProposalStatus.ACCEPTED);
            usedItemProposalRepository.save(proposal);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 수락할 수 있습니다.");
        }
    }


    /**
     * 구매 제안 거절
     * @param proposalId
     */
    public void rejectProposal(Long itemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
        // 물품 조회
        Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "물품 정보를 찾을 수 없습니다.");
        UsedItem usedItem = optionalItem.get(); // 해피해킹

        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get(); // USER1

        // 해당 물품을 등록한 사용자라면
        if (usedItem.getWriter().getId().equals(userEntity.getId())) {
            log.info("당신은 이 물품을 등록한 {}입니다. (제안 거절)", usedItem.getWriter().getUsername());
            // 구매 제안 정보
            Optional<UsedItemProposal> optionalProposal = usedItemProposalRepository.findById(proposalId);
            if (optionalProposal.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 구매 제안을 찾을 수 없습니다.");
            UsedItemProposal proposal = optionalProposal.get();
            // 구매 제인을 수락상태로 변경
            proposal.setStatus(ProposalStatus.REJECTED);
            usedItemProposalRepository.save(proposal);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 거절할 수 있습니다.");
        }

    }

    // 제안을 등록한 사용자의 구매 확정
    // 좀 복잡하다.
    public void confirmProposal(Long itemId, Long proposalId) {
        // 물품 조회
        Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "물품 정보를 찾을 수 없습니다.");
        UsedItem usedItem = optionalItem.get(); // 해피해킹

        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get(); // USER1


        // 구매 제안 정보 조회
        Optional<UsedItemProposal> optionalProposal = usedItemProposalRepository.findById(proposalId);
        if (optionalProposal.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 구매 제안을 찾을 수 없습니다.");
        UsedItemProposal proposal = optionalProposal.get();


        // 해당 물품을 구매 제안한 사용자라면?
        // 구매 제안 정보중 proposer의 id 가 userEntity 의 id 와 같다면
        if (proposal.getProposer().getId().equals(userEntity.getId())) {
            // 구매 제안 상태가 "ACCEPTED" 일 경우 아래 진행 (따로 조건문은 작성하지 않음)
            // 1. 구매 제안 상태를 "확정 상태" 로 변경
            proposal.setStatus(ProposalStatus.CONFIRMED);
            usedItemProposalRepository.save(proposal);

            // 2. 구매 제안이 "확정 상태"가 될 경우, 물품의 상태는 판매 완료가 된다.
            usedItem.setStatus("판매완료");
            usedItemRepository.save(usedItem);

            // 3. 구매 제안이 "확정 상태"가 될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 거절된다.
            List<UsedItemProposal> otherProposals = usedItemProposalRepository.findByUsedItemAndIdNot(usedItem, proposalId);
            for (UsedItemProposal otherProposal : otherProposals) {
                otherProposal.setStatus(ProposalStatus.REJECTED);
                usedItemProposalRepository.save(otherProposal);
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매를 확정할 수 있습니다.");
        }

    }
}
