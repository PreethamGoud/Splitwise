package com.project.splitwise.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDto {
	
	private Long userId;
    private BigDecimal paidAmount;
    private BigDecimal owedAmount; //required as an input in exactSplit strategy.
    private Boolean includedInSplit; //required as an input in equalSplit Strategy.
    private BigDecimal percentage;  //required as an input in percentageSplit strategy.
    
}
