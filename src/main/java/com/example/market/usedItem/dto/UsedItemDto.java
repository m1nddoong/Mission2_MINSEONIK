package com.example.market.usedItem.dto;

import com.example.market.usedItem.entity.UsedItem;
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
public class UsedItemDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Integer price;
    private String status;
    private Long writerId;

    public static UsedItemDto fromEntity(UsedItem usedItem) {
        return UsedItemDto.builder()
                .id(usedItem.getId())
                .title(usedItem.getTitle())
                .content(usedItem.getContent())
                .price(usedItem.getPrice())
                .imageUrl(usedItem.getImageUrl())
                .status(usedItem.getStatus())
                .writerId(usedItem.getWriter().getId())
                .build();
    }
}