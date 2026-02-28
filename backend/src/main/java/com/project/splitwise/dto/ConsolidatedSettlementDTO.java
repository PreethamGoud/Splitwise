package com.project.splitwise.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
@NoArgsConstructor
public class ConsolidatedSettlementDTO {
    private String paidFrom;
    private String paidTo;
    private BigDecimal totalAmount;

    List<ConsolidatedSettlementMiniDTO> individualGroupShare = new ArrayList<>();

    public ConsolidatedSettlementDTO(String a , String b , BigDecimal c){
        this.paidFrom = a;
        this.paidTo = b;
        this.totalAmount = c;
    }

    public ConsolidatedSettlementDTO(BigDecimal a){
        this.totalAmount = a;
    }
}
