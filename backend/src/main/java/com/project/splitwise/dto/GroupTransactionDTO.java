package com.project.splitwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@AllArgsConstructor
public class GroupTransactionDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date date;
    private String type; // EXPENSE or SETTLEMENT
    private String description;
    private String paidFrom;
    private String paidTo; // may be null for expenses
    private BigDecimal amount;
}

