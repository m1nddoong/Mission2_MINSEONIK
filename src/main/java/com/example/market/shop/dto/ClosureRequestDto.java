package com.example.market.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosureRequestDto {
    private String text;
}
