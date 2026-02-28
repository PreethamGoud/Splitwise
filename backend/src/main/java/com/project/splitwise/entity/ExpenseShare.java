package com.project.splitwise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class ExpenseShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal shareAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceLeft;
    
    @Enumerated(EnumType.STRING)
    private ShareStatus status; // OWES, PAID, PARTIALLY_PAID

    @ManyToOne
    private User user;

    @ManyToOne
    private Expense expense;

    public ExpenseShare(BigDecimal a , BigDecimal b , ShareStatus c , User d , Expense e){
        this.shareAmount=a;
        this.paidAmount=b;
        this.status = c;
        this.user = d;
        this.expense=e;
    }

    // Constructors, getters, setters
}
