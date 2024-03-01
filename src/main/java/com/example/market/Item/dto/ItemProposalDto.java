package com.example.market.Item.dto;

import com.example.market.Item.entity.ItemProposal;
import com.example.market.Item.entity.ProposalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class ItemProposalDto {
    private Long id;
    private Long itemId;
    private Long proposerId;
    private ProposalStatus status;


    public static ItemProposalDto fromEntity(ItemProposal itemProposal) {
        return ItemProposalDto.builder()
                .id(itemProposal.getId())
                .itemId(itemProposal.getItem().getId())
                .proposerId(itemProposal.getProposer().getId())
                .status(itemProposal.getStatus())
                .build();
    }


//    public static ItemProposalDto fromEntity(ItemProposal itemProposal) {
//        return new ItemProposalDto(
//                itemProposal.getId(),
//                itemProposal.getItem().getId(),
//                itemProposal.getProposer().getId(),
//                itemProposal.getStatus()
//        );
//    }
}
