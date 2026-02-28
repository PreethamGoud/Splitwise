package com.project.splitwise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"users", "expenses"})
@Table(name = "groups") // Explicit table name
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String groupName;
    private String description;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<UserGroup> users = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>();

    // Constructors, getters, setters

}
