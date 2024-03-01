package com.example.market.Item.repo;

import com.example.market.Item.entity.Item;
import com.example.market.Item.entity.ItemProposal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemProposalRepository extends JpaRepository<ItemProposal, Long> {
    List<ItemProposal> findByItemAndProposerUsername(Item item, String username);

    List<ItemProposal> findByItem(Item item);

    // item 에 해당하는 모든 구매 제안을 찾되,
    // proposalId 와 일치하지 않는 구매 제안 반환
    List<ItemProposal> findByItemAndIdNot(Item item, Long proposalId);
}
