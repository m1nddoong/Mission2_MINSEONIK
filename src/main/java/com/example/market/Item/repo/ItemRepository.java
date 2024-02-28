package com.example.market.Item.repo;

import com.example.market.Item.entity.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
