package com.project.splitwise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@ToString(exclude = {"shares"})
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private BigDecimal amount;
    private Date date;
    
    @Enumerated(EnumType.STRING)
    private ExpenseStatus status; // PENDING, SETTLED,PARTIALLY_SETTLED
    
    @Enumerated(EnumType.STRING)
    private SplitType splitType; // EQUAL,EXACT,PERCENTAGE
    
    @ManyToOne
    private User payer;

    @ManyToOne
    private Group group;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpenseShare> shares = new ArrayList<>();

    // Constructors, getters, setters
    public Expense(String a,BigDecimal b,Date c,ExpenseStatus d,SplitType e,User f,Group g){
        this.description = a;
        this.amount = b;
        this.date = c;
        this.status=d;
        this.splitType = e;
        this.payer = f;
        this.group=g;
    }

}
