package com.project.splitwise.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Data
@NoArgsConstructor
public class ConsolidatedSettlementMiniDTO {
    private String paidFrom;
    private String paidTo;
    private BigDecimal amount;
    private String groupName;

    public ConsolidatedSettlementMiniDTO(String a,String b,BigDecimal c,String d){
        this.paidFrom = a;
        this.paidTo = b;
        this.amount = c;
        this.groupName = d;
    }

    public ConsolidatedSettlementMiniDTO(SettleUpDTO a,String d){
        this.paidFrom = a.getPaidFrom();
        this.paidTo = a.getPaidTo();
        this.amount = a.getAmount();
        this.groupName = d;
    }
}
