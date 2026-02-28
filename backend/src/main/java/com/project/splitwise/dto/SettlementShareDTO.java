package com.project.splitwise.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementShareDTO {
    private String userName;
    private BigDecimal userShare;

    public SettlementShareDTO(String a , BigDecimal b){
        this.userName = a;
        this.userShare = b;
    }
}
