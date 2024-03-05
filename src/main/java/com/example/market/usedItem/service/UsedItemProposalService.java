package com.example.market.usedItem.service;


import static com.example.market.usedItem.entity.QUsedItem.usedItem;

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
     * @param usedItemId
     * @return
     */
    public void createProposal(Long usedItemId) {
        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = getCurrentUserFromUsername(username);
        // 물품 조회
        UsedItem usedItemEntity = getUsedItemFromId(usedItemId);

        // 해당 물품을 등록한 사용자는 제외
        if (!userEntity.getId().equals(usedItemEntity.getWriter().getId())) {
            // 구매 제안 등록
            UsedItemProposal proposal = new UsedItemProposal();
            proposal.setUsedItem(usedItemEntity);
            proposal.setProposer(userEntity); // UserEntity 객체 설정
            proposal.setStatus(ProposalStatus.PENDING); // 대기 중 상태로 등록
            usedItemProposalRepository.save(proposal);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사람은 구매 제안을 등록할 수 없습니다.");
        }
    }


    /**
     * 구매 제안 확인
     * 등록된 구매 제안은 물품을 등록한 사용자와 제안을 등록한 사용자만 조회 가능
     * @param usedItemId
     * @return
     */
    public List<UsedItemProposalDto> getItemProposals(Long usedItemId) {
        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = getCurrentUserFromUsername(username);
        // 물품 조회
        UsedItem usedItemEntity = getUsedItemFromId(usedItemId);

        // 해당 물품을 등록한 사용자라면
        if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
            // 해당 물품에 대한 다른 사람들의 모든 구매 제안을 확인 가능
            List<UsedItemProposal> allProposals = usedItemProposalRepository.findByUsedItem(usedItemEntity);
            return allProposals.stream()
                    .map(UsedItemProposalDto::fromEntity)
                    .collect(Collectors.toList());
        }
        // 해당 물품을 구매 제안한 사람이라면
        else {
            List<UsedItemProposal> userProposals = usedItemProposalRepository.findByUsedItemAndProposerUsername(usedItemEntity, username);
            return userProposals.stream()
                    .map(UsedItemProposalDto::fromEntity)
                    .collect(Collectors.toList());
        }
    }


    /**
     * 구매 제안 수락
     * @param usedItemId
     * @param proposalId
     */
    public void acceptProposal(Long usedItemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = getCurrentUserFromUsername(username);
        // 물품 조회
        UsedItem usedItemEntity = getUsedItemFromId(usedItemId);
        // 물품 구매 제안 조회
        UsedItemProposal proposalEntity = getUsedItemProposalFromId(proposalId);

        // 해당 물품을 등록한 사용자라면
        if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
            // 구매 제인을 수락상태로 변경
            proposalEntity.setStatus(ProposalStatus.ACCEPTED);
            usedItemProposalRepository.save(proposalEntity);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 수락할 수 있습니다.");
        }
    }


    /**
     * 구매 제안 거절
     * @param proposalId
     */
    public void rejectProposal(Long usedItemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = getCurrentUserFromUsername(username);
        // 물품 조회
        UsedItem usedItemEntity = getUsedItemFromId(usedItemId);
        // 물품 구매 제안 조회
        UsedItemProposal proposalEntity = getUsedItemProposalFromId(proposalId);

        // 해당 물품을 등록한 사용자라면
        if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
            // 구매 제인을 수락상태로 변경
            proposalEntity.setStatus(ProposalStatus.REJECTED);
            usedItemProposalRepository.save(proposalEntity);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 거절할 수 있습니다.");
        }
    }

    // 제안을 등록한 사용자의 구매 확정
    // 좀 복잡하다.
    public void confirmProposal(Long usedItemId, Long proposalId) {
        // 현재 인증된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = getCurrentUserFromUsername(username);
        // 물품 조회
        UsedItem usedItemEntity = getUsedItemFromId(usedItemId);
        // 물품 구매 제안 조회
        UsedItemProposal proposalEntity = getUsedItemProposalFromId(proposalId);

        // 해당 물품을 구매 제안한 사용자라면
        if (proposalEntity.getProposer().getId().equals(userEntity.getId())) {
            // 구매 제안 상태가 "ACCEPTED" 일 경우 아래 진행 (따로 조건문은 작성하지 않음)
            // 1. 구매 제안 상태를 "확정 상태" 로 변경
            proposalEntity.setStatus(ProposalStatus.CONFIRMED);
            usedItemProposalRepository.save(proposalEntity);

            // 2. 구매 제안이 "확정 상태"가 될 경우, 물품의 상태는 판매 완료가 된다.
            usedItemEntity.setStatus("판매완료");
            usedItemRepository.save(usedItemEntity);

            // 3. 구매 제안이 "확정 상태"가 될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 거절된다.
            List<UsedItemProposal> otherProposals = usedItemProposalRepository.findByUsedItemAndIdNot(usedItemEntity, proposalId);
            for (UsedItemProposal otherProposal : otherProposals) {
                otherProposal.setStatus(ProposalStatus.REJECTED);
                usedItemProposalRepository.save(otherProposal);
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매를 확정할 수 있습니다.");
        }
    }


     /*
    <===================================================================>
    중복을 피하기 위해 사용자 객체, 중고 물품 객체를 가져오는 부분을 따로 메서드로 추출
    <===================================================================>
     */

    private UserEntity getCurrentUserFromUsername(String username) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        return optionalUserEntity.get();
    }

    private UsedItem getUsedItemFromId(Long usedItemId) {
        // 물품 정보 조회
        Optional<UsedItem> optionalUsedItem = usedItemRepository.findById(usedItemId);
        if (optionalUsedItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "물품 정보를 찾을 수 없습니다.");
        return optionalUsedItem.get();
    }

    private UsedItemProposal getUsedItemProposalFromId(Long proposalId) {
        // 구매 제안 정보
        Optional<UsedItemProposal> optionalProposal = usedItemProposalRepository.findById(proposalId);
        if (optionalProposal.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 구매 제안을 찾을 수 없습니다.");
        return optionalProposal.get();
    }

}
