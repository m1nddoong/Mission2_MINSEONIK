package com.example.market.usedItem.repo;

import com.example.market.usedItem.entity.UsedItem;
import com.example.market.usedItem.entity.UsedItemProposal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsedItemProposalRepository extends JpaRepository<UsedItemProposal, Long> {
    List<UsedItemProposal> findByUsedItemAndProposerUsername(UsedItem usedItem, String username);

    List<UsedItemProposal> findByUsedItem(UsedItem usedItem);

    // item 에 해당하는 모든 구매 제안을 찾되,
    // proposalId 와 일치하지 않는 구매 제안 반환
    List<UsedItemProposal> findByUsedItemAndIdNot(UsedItem usedItem, Long proposalId);
}
