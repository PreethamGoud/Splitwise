package com.project.splitwise.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleUpDTO {
    private String paidFrom;
    private String paidTo;
    private BigDecimal amount;


    public SettleUpDTO(String a,String b,BigDecimal c){
        this.paidFrom = a;
        this.paidTo = b;
        this.amount = c;
    }
}
