package com.example.market.usedItem.entity;


import com.example.market.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "used_item_proposal")
public class UsedItemProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 물품
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private UsedItem usedItem;

    // 물품 구매를 제안한 사람
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proposer_id", nullable = false)
    private UserEntity proposer;

    // 제안 상태
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;
}

