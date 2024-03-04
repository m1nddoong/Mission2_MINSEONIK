package com.example.market.usedItem.dto;

import com.example.market.usedItem.entity.UsedItemProposal;
import com.example.market.usedItem.entity.ProposalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class UsedItemProposalDto {
    private Long id;
    private Long itemId;
    private Long proposerId;
    private ProposalStatus status;


    public static UsedItemProposalDto fromEntity(UsedItemProposal usedItemProposal) {
        return UsedItemProposalDto.builder()
                .id(usedItemProposal.getId())
                .itemId(usedItemProposal.getUsedItem().getId())
                .proposerId(usedItemProposal.getProposer().getId())
                .status(usedItemProposal.getStatus())
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
