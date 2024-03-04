package com.example.market.usedItem.repo;

import com.example.market.usedItem.entity.UsedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsedItemRepository extends JpaRepository<UsedItem, Long> {
}
