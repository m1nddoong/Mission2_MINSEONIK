package com.example.market.Item.controller;

import com.example.market.Item.dto.ItemDto;
import com.example.market.Item.service.ItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> readAll() {
        return itemService.readAll();
    }

    @GetMapping("{id}")
    public ItemDto readOne(
            @PathVariable("id")
            Long id
    ) {
        return itemService.readOne(id);
    }
}
