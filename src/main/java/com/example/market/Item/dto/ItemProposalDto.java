package com.example.market.Item.dto;

import com.example.market.Item.entity.ItemProposal;
import com.example.market.Item.entity.ProposalStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ItemProposalDto {
    private Long id;
    private Long itemId;
    private Long proposerId;
    private ProposalStatus status;

    public static ItemProposalDto fromEntity(ItemProposal itemProposal) {
        return new ItemProposalDto(
                itemProposal.getId(),
                itemProposal.getItem().getId(),
                itemProposal.getProposer().getId(),
                itemProposal.getStatus()
        );
    }
}
