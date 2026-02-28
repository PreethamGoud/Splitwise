package com.project.splitwise.dto;

import java.math.BigDecimal;
import java.util.List;

import com.project.splitwise.entity.SplitType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExpenseDto {
      
	private String description;
    private BigDecimal totalAmount;
    private Long groupId;
    private Long createdByUserId;
    private SplitType splitType;

    //Participants for particular Expense 
    private List<ParticipantDto> participants;
}
