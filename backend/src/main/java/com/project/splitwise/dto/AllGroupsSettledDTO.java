package com.project.splitwise.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Data
@NoArgsConstructor
public class AllGroupsSettledDTO {
    private BigDecimal grandTotalAmount;
    private List<ConsolidatedSettlementDTO> shareWithFriends;

    public AllGroupsSettledDTO(BigDecimal a , List<ConsolidatedSettlementDTO> b){
        this.grandTotalAmount = a;
        this.shareWithFriends = b;
    }
}
