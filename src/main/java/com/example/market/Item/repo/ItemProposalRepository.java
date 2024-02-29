package com.example.market.Item.repo;

import com.example.market.Item.entity.Item;
import com.example.market.Item.entity.ItemProposal;
import com.example.market.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemProposalRepository extends JpaRepository<ItemProposal, Long> {
    List<ItemProposal> findByItemAndProposerUsername(Item item, String username);

    List<ItemProposal> findByItem(Item item);

}
