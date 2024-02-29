package com.example.market.Item.dto;

import com.example.market.Item.entity.Item;
import com.example.market.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Integer price;
    private String status;
    private Long writerId;

    public static ItemDto fromEntity(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .writerId(item.getWriter().getId())
                .build();
    }
}