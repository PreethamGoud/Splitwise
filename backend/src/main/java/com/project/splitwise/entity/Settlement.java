package com.project.splitwise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString(exclude = {"payer", "receiver","group"})
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private Date date;
    private String description;

    @ManyToOne
    private User payer;

    @ManyToOne
    private User receiver;

    @ManyToOne
    private Group group;

    // Constructors, getters, setters
    public Settlement(BigDecimal a,Date b,String c,User d , User e,Group f){
        this.amount=a;
        this.date=b;
        this.description=c;
        this.payer=d;
        this.receiver=e;
        this.group=f;
    }
}
